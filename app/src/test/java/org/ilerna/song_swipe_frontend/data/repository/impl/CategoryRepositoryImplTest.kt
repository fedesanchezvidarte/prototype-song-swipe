package org.ilerna.song_swipe_frontend.data.repository.impl

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.data.datasource.remote.api.SpotifyApi
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoriesResponse
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoryDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoryPlaylistsResponse
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyExternalUrlsDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyFeaturedPlaylistsResponse
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPaginatedCategoriesDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPaginatedPlaylistsDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistOwnerDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistTracksRefDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifySimplifiedPlaylistDto
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for CategoryRepositoryImpl
 * Tests mock data provider and Spotify API integration for music categories
 */
class CategoryRepositoryImplTest {

    private lateinit var repository: CategoryRepositoryImpl
    private lateinit var mockSpotifyApi: SpotifyApi

    @Before
    fun setUp() {
        mockSpotifyApi = mockk()
        repository = CategoryRepositoryImpl(mockSpotifyApi)
    }

    // ==================== getCategories Tests (Mock Data) ====================

    @Test
    fun `getCategories should return all mock categories`() = runTest {
        val result = repository.getCategories()

        assertTrue(result.isSuccess)
        val categories = result.getOrNull()
        assertNotNull(categories)
        assertEquals(10, categories.size)
    }

    @Test
    fun `getCategories should return expected category names`() = runTest {
        val categories = repository.getCategories().getOrNull()!!
        val expectedNames = listOf(
            "Pop", "Rock", "Electronic", "Hip Hop", "Jazz",
            "Classical", "R&B", "Country", "Latin", "Indie"
        )

        assertEquals(expectedNames, categories.map { it.name })
    }

    // ==================== getCategoryById Tests ====================

    @Test
    fun `getCategoryById should return category for valid ID`() = runTest {
        val result = repository.getCategoryById("1")
        val category = result.getOrNull()

        assertNotNull(category)
        assertEquals("1", category.id)
        assertEquals("Pop", category.name)
    }

    @Test
    fun `getCategoryById should return null for unknown ID`() = runTest {
        val result = repository.getCategoryById("999")

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    // ==================== Data Consistency Test ====================

    @Test
    fun `getCategoryById results should match getCategories results`() = runTest {
        val allCategories = repository.getCategories().getOrNull()!!

        allCategories.forEach { expected ->
            val byId = repository.getCategoryById(expected.id).getOrNull()
            assertNotNull(byId)
            assertEquals(expected.id, byId.id)
            assertEquals(expected.name, byId.name)
        }
    }

    // ==================== getCategoriesFromApi Tests ====================

    @Test
    fun `getCategoriesFromApi should return Success when API responds successfully`() = runTest {
        // Given
        val categoriesResponse = createCategoriesResponse(
            listOf(
                createCategoryDto("pop", "Pop"),
                createCategoryDto("rock", "Rock")
            )
        )
        coEvery { mockSpotifyApi.getCategories(any(), any(), any()) } returns Response.success(categoriesResponse)

        // When
        val result = repository.getCategoriesFromApi()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, result.data.size)
        assertEquals("pop", result.data[0].id)
        assertEquals("Pop", result.data[0].name)
    }

    @Test
    fun `getCategoriesFromApi should map icon URL correctly`() = runTest {
        // Given
        val categoryWithIcon = createCategoryDto("jazz", "Jazz", iconUrl = "https://icon.url/jazz.jpg")
        val categoriesResponse = createCategoriesResponse(listOf(categoryWithIcon))
        coEvery { mockSpotifyApi.getCategories(any(), any(), any()) } returns Response.success(categoriesResponse)

        // When
        val result = repository.getCategoriesFromApi()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals("https://icon.url/jazz.jpg", result.data[0].iconUrl)
    }

    @Test
    fun `getCategoriesFromApi should return Error when API fails`() = runTest {
        // Given
        coEvery { mockSpotifyApi.getCategories(any(), any(), any()) } returns Response.error(
            401,
            "Unauthorized".toResponseBody()
        )

        // When
        val result = repository.getCategoriesFromApi()

        // Then
        assertTrue(result is NetworkResult.Error)
    }

    // ==================== getCategoryPlaylists Tests ====================

    @Test
    fun `getCategoryPlaylists should return Success when API responds successfully`() = runTest {
        // Given
        val playlistsResponse = createCategoryPlaylistsResponse(
            listOf(
                createSimplifiedPlaylistDto("playlist1", "Pop Hits"),
                createSimplifiedPlaylistDto("playlist2", "Pop Classics")
            )
        )
        coEvery { mockSpotifyApi.getCategoryPlaylists(any(), any(), any()) } returns Response.success(playlistsResponse)

        // When
        val result = repository.getCategoryPlaylists("pop")

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, result.data.size)
        assertEquals("playlist1", result.data[0].id)
        assertEquals("Pop Hits", result.data[0].name)
    }

    @Test
    fun `getCategoryPlaylists should filter out null playlists`() = runTest {
        // Given
        val playlistsResponse = createCategoryPlaylistsResponse(
            listOf(
                createSimplifiedPlaylistDto("playlist1", "Valid Playlist"),
                null
            )
        )
        coEvery { mockSpotifyApi.getCategoryPlaylists(any(), any(), any()) } returns Response.success(playlistsResponse)

        // When
        val result = repository.getCategoryPlaylists("pop")

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(1, result.data.size)
    }

    @Test
    fun `getCategoryPlaylists should return Error when API fails`() = runTest {
        // Given
        coEvery { mockSpotifyApi.getCategoryPlaylists(any(), any(), any()) } returns Response.error(
            404,
            "Category not found".toResponseBody()
        )

        // When
        val result = repository.getCategoryPlaylists("invalid_category")

        // Then
        assertTrue(result is NetworkResult.Error)
    }

    // ==================== getFeaturedPlaylists Tests ====================

    @Test
    fun `getFeaturedPlaylists should return Success when API responds successfully`() = runTest {
        // Given
        val featuredResponse = createFeaturedPlaylistsResponse(
            listOf(
                createSimplifiedPlaylistDto("featured1", "Today's Top Hits"),
                createSimplifiedPlaylistDto("featured2", "Discover Weekly")
            )
        )
        coEvery { mockSpotifyApi.getFeaturedPlaylists(any(), any(), any()) } returns Response.success(featuredResponse)

        // When
        val result = repository.getFeaturedPlaylists()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, result.data.size)
        assertEquals("featured1", result.data[0].id)
        assertEquals("Today's Top Hits", result.data[0].name)
    }

    @Test
    fun `getFeaturedPlaylists should return Error when API fails`() = runTest {
        // Given
        coEvery { mockSpotifyApi.getFeaturedPlaylists(any(), any(), any()) } returns Response.error(
            500,
            "Internal server error".toResponseBody()
        )

        // When
        val result = repository.getFeaturedPlaylists()

        // Then
        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `getFeaturedPlaylists should handle network exception`() = runTest {
        // Given
        coEvery { mockSpotifyApi.getFeaturedPlaylists(any(), any(), any()) } throws Exception("Network error")

        // When
        val result = repository.getFeaturedPlaylists()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertTrue(result.message.contains("Network error"))
    }

    // ==================== Helper Functions ====================

    private fun createCategoryDto(
        id: String,
        name: String,
        iconUrl: String? = null
    ): SpotifyCategoryDto {
        return SpotifyCategoryDto(
            id = id,
            name = name,
            href = "https://api.spotify.com/v1/browse/categories/$id",
            icons = iconUrl?.let { listOf(SpotifyImageDto(it, 300, 300)) }
        )
    }

    private fun createCategoriesResponse(categories: List<SpotifyCategoryDto>): SpotifyCategoriesResponse {
        return SpotifyCategoriesResponse(
            categories = SpotifyPaginatedCategoriesDto(
                items = categories,
                href = "https://api.spotify.com/v1/browse/categories",
                limit = 20,
                offset = 0,
                total = categories.size,
                next = null,
                previous = null
            )
        )
    }

    private fun createSimplifiedPlaylistDto(
        id: String,
        name: String,
        imageUrl: String? = "https://image.url/$id.jpg"
    ): SpotifySimplifiedPlaylistDto {
        return SpotifySimplifiedPlaylistDto(
            id = id,
            name = name,
            description = "Description for $name",
            images = imageUrl?.let { listOf(SpotifyImageDto(it, 300, 300)) },
            owner = SpotifyPlaylistOwnerDto(
                id = "owner1",
                displayName = "Test Owner",
                externalUrls = SpotifyExternalUrlsDto("https://open.spotify.com/user/owner1"),
                href = null,
                uri = null
            ),
            public = true,
            collaborative = false,
            snapshotId = "snapshot123",
            uri = "spotify:playlist:$id",
            externalUrls = SpotifyExternalUrlsDto("https://open.spotify.com/playlist/$id"),
            href = "https://api.spotify.com/v1/playlists/$id",
            tracks = SpotifyPlaylistTracksRefDto(
                href = "https://api.spotify.com/v1/playlists/$id/tracks",
                total = 50
            )
        )
    }

    private fun createCategoryPlaylistsResponse(
        playlists: List<SpotifySimplifiedPlaylistDto?>
    ): SpotifyCategoryPlaylistsResponse {
        return SpotifyCategoryPlaylistsResponse(
            message = "Popular Playlists",
            playlists = SpotifyPaginatedPlaylistsDto(
                items = playlists,
                href = "https://api.spotify.com/v1/browse/categories/pop/playlists",
                limit = 20,
                offset = 0,
                total = playlists.filterNotNull().size,
                next = null,
                previous = null
            )
        )
    }

    private fun createFeaturedPlaylistsResponse(
        playlists: List<SpotifySimplifiedPlaylistDto?>
    ): SpotifyFeaturedPlaylistsResponse {
        return SpotifyFeaturedPlaylistsResponse(
            message = "Featured Playlists",
            playlists = SpotifyPaginatedPlaylistsDto(
                items = playlists,
                href = "https://api.spotify.com/v1/browse/featured-playlists",
                limit = 20,
                offset = 0,
                total = playlists.filterNotNull().size,
                next = null,
                previous = null
            )
        )
    }
}

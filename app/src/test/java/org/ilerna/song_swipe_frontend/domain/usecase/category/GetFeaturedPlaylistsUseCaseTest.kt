package org.ilerna.song_swipe_frontend.domain.usecase.category

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.SimplifiedPlaylist
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for GetFeaturedPlaylistsUseCase
 * Tests the use case that fetches featured playlists from Spotify
 */
class GetFeaturedPlaylistsUseCaseTest {

    private lateinit var useCase: GetFeaturedPlaylistsUseCase
    private lateinit var mockCategoryRepository: CategoryRepository

    @Before
    fun setup() {
        mockCategoryRepository = mockk()
        useCase = GetFeaturedPlaylistsUseCase(mockCategoryRepository)
    }

    // ==================== invoke Tests ====================

    @Test
    fun `invoke should return playlists when repository succeeds`() = runTest {
        // Given
        val expectedPlaylists = listOf(
            createSimplifiedPlaylist("featured1", "Today's Top Hits"),
            createSimplifiedPlaylist("featured2", "Discover Weekly")
        )
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Success(expectedPlaylists)

        // When
        val result = useCase()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, result.data.size)
        assertEquals("featured1", result.data[0].id)
    }

    @Test
    fun `invoke should pass locale and limit to repository`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Success(emptyList())

        // When
        useCase(locale = "es_ES", limit = 10)

        // Then
        coVerify { mockCategoryRepository.getFeaturedPlaylists("es_ES", 10) }
    }

    @Test
    fun `invoke should use default parameters when not specified`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Success(emptyList())

        // When
        useCase()

        // Then
        coVerify { mockCategoryRepository.getFeaturedPlaylists(null, 20) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Error(
            "Service unavailable",
            503
        )

        // When
        val result = useCase()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Service unavailable", result.message)
        assertEquals(503, result.code)
    }

    // ==================== getRandomPlaylist Tests ====================

    @Test
    fun `getRandomPlaylist should return a random playlist when playlists exist`() = runTest {
        // Given
        val playlists = listOf(
            createSimplifiedPlaylist("featured1", "Today's Top Hits"),
            createSimplifiedPlaylist("featured2", "Discover Weekly"),
            createSimplifiedPlaylist("featured3", "Release Radar")
        )
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Success(playlists)

        // When
        val result = useCase.getRandomPlaylist()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertTrue(playlists.any { it.id == result.data.id })
    }

    @Test
    fun `getRandomPlaylist should return error when no playlists available`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Success(emptyList())

        // When
        val result = useCase.getRandomPlaylist()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("No featured playlists available", result.message)
    }

    @Test
    fun `getRandomPlaylist should return error when repository fails`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Error(
            "Network error",
            null
        )

        // When
        val result = useCase.getRandomPlaylist()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Network error", result.message)
    }

    @Test
    fun `getRandomPlaylist should return single playlist when only one exists`() = runTest {
        // Given
        val singlePlaylist = createSimplifiedPlaylist("only_featured", "Only Featured")
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Success(listOf(singlePlaylist))

        // When
        val result = useCase.getRandomPlaylist()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals("only_featured", result.data.id)
    }

    @Test
    fun `getRandomPlaylist should pass locale to repository`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getFeaturedPlaylists(any(), any()) } returns NetworkResult.Success(
            listOf(createSimplifiedPlaylist("playlist1", "Test"))
        )

        // When
        useCase.getRandomPlaylist(locale = "en_US", limit = 5)

        // Then
        coVerify { mockCategoryRepository.getFeaturedPlaylists("en_US", 5) }
    }

    // ==================== Helper Functions ====================

    private fun createSimplifiedPlaylist(
        id: String,
        name: String
    ): SimplifiedPlaylist {
        return SimplifiedPlaylist(
            id = id,
            name = name,
            description = "Description for $name",
            imageUrl = "https://image.url/$id.jpg",
            ownerName = "Spotify",
            totalTracks = 100
        )
    }
}

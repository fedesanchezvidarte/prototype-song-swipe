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
 * Unit tests for GetCategoryPlaylistsUseCase
 * Tests the use case that fetches playlists from a Spotify category
 */
class GetCategoryPlaylistsUseCaseTest {

    private lateinit var useCase: GetCategoryPlaylistsUseCase
    private lateinit var mockCategoryRepository: CategoryRepository

    @Before
    fun setup() {
        mockCategoryRepository = mockk()
        useCase = GetCategoryPlaylistsUseCase(mockCategoryRepository)
    }

    // ==================== invoke Tests ====================

    @Test
    fun `invoke should return playlists when repository succeeds`() = runTest {
        // Given
        val expectedPlaylists = listOf(
            createSimplifiedPlaylist("playlist1", "Pop Hits"),
            createSimplifiedPlaylist("playlist2", "Pop Classics")
        )
        coEvery { mockCategoryRepository.getCategoryPlaylists(any(), any()) } returns NetworkResult.Success(expectedPlaylists)

        // When
        val result = useCase("pop")

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, result.data.size)
        assertEquals("playlist1", result.data[0].id)
    }

    @Test
    fun `invoke should pass categoryId to repository`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getCategoryPlaylists(any(), any()) } returns NetworkResult.Success(emptyList())

        // When
        useCase("rock", limit = 10)

        // Then
        coVerify { mockCategoryRepository.getCategoryPlaylists("rock", 10) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getCategoryPlaylists(any(), any()) } returns NetworkResult.Error(
            "Category not found",
            404
        )

        // When
        val result = useCase("invalid_category")

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Category not found", result.message)
        assertEquals(404, result.code)
    }

    // ==================== getRandomPlaylist Tests ====================

    @Test
    fun `getRandomPlaylist should return a random playlist when playlists exist`() = runTest {
        // Given
        val playlists = listOf(
            createSimplifiedPlaylist("playlist1", "Pop Hits"),
            createSimplifiedPlaylist("playlist2", "Pop Classics"),
            createSimplifiedPlaylist("playlist3", "Pop Rising")
        )
        coEvery { mockCategoryRepository.getCategoryPlaylists(any(), any()) } returns NetworkResult.Success(playlists)

        // When
        val result = useCase.getRandomPlaylist("pop")

        // Then
        assertTrue(result is NetworkResult.Success)
        assertTrue(playlists.any { it.id == result.data.id })
    }

    @Test
    fun `getRandomPlaylist should return error when no playlists found`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getCategoryPlaylists(any(), any()) } returns NetworkResult.Success(emptyList())

        // When
        val result = useCase.getRandomPlaylist("empty_category")

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("No playlists found for this category", result.message)
    }

    @Test
    fun `getRandomPlaylist should return error when repository fails`() = runTest {
        // Given
        coEvery { mockCategoryRepository.getCategoryPlaylists(any(), any()) } returns NetworkResult.Error(
            "API error",
            500
        )

        // When
        val result = useCase.getRandomPlaylist("pop")

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("API error", result.message)
    }

    @Test
    fun `getRandomPlaylist should return single playlist when only one exists`() = runTest {
        // Given
        val singlePlaylist = createSimplifiedPlaylist("only_playlist", "Only Playlist")
        coEvery { mockCategoryRepository.getCategoryPlaylists(any(), any()) } returns NetworkResult.Success(listOf(singlePlaylist))

        // When
        val result = useCase.getRandomPlaylist("single")

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals("only_playlist", result.data.id)
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
            ownerName = "Test Owner",
            totalTracks = 50
        )
    }
}

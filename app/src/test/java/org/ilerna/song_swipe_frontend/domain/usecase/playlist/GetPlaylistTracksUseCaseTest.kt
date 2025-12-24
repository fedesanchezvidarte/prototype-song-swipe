package org.ilerna.song_swipe_frontend.domain.usecase.playlist

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.Album
import org.ilerna.song_swipe_frontend.domain.model.Artist
import org.ilerna.song_swipe_frontend.domain.model.Playlist
import org.ilerna.song_swipe_frontend.domain.model.Track
import org.ilerna.song_swipe_frontend.domain.repository.PlaylistRepository
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for GetPlaylistTracksUseCase
 * Tests the use case that fetches tracks from a Spotify playlist
 */
class GetPlaylistTracksUseCaseTest {

    private lateinit var useCase: GetPlaylistTracksUseCase
    private lateinit var mockPlaylistRepository: PlaylistRepository

    @Before
    fun setup() {
        mockPlaylistRepository = mockk()
        useCase = GetPlaylistTracksUseCase(mockPlaylistRepository)
    }

    @Test
    fun `invoke should return tracks when repository succeeds`() = runTest {
        // Given
        val expectedTracks = listOf(createTrack("track1"), createTrack("track2"))
        val playlist = createPlaylist(tracks = expectedTracks)
        coEvery { mockPlaylistRepository.getPlaylist(any()) } returns NetworkResult.Success(playlist)

        // When
        val result = useCase()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(2, result.data.size)
        assertEquals("track1", result.data[0].id)
    }

    @Test
    fun `invoke should use default playlist ID when not specified`() = runTest {
        // Given
        val playlist = createPlaylist()
        coEvery { mockPlaylistRepository.getPlaylist(any()) } returns NetworkResult.Success(playlist)

        // When
        useCase()

        // Then
        coVerify { mockPlaylistRepository.getPlaylist(GetPlaylistTracksUseCase.DEFAULT_PLAYLIST_ID) }
    }

    @Test
    fun `invoke should use custom playlist ID when specified`() = runTest {
        // Given
        val customPlaylistId = "customPlaylist123"
        val playlist = createPlaylist()
        coEvery { mockPlaylistRepository.getPlaylist(any()) } returns NetworkResult.Success(playlist)

        // When
        useCase(customPlaylistId)

        // Then
        coVerify { mockPlaylistRepository.getPlaylist(customPlaylistId) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        coEvery { mockPlaylistRepository.getPlaylist(any()) } returns NetworkResult.Error(
            "API error",
            401
        )

        // When
        val result = useCase()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("API error", result.message)
        assertEquals(401, result.code)
    }

    // ==================== Helper Functions ====================

    private fun createPlaylist(
        tracks: List<Track> = listOf(createTrack("track1"))
    ): Playlist {
        return Playlist(
            id = "playlist123",
            name = "Test Playlist",
            description = "A test playlist",
            imageUrl = "https://image.url/playlist.jpg",
            tracks = tracks,
            totalTracks = tracks.size
        )
    }

    private fun createTrack(id: String): Track {
        return Track(
            id = id,
            name = "Track $id",
            artists = listOf(Artist("artist1", "Test Artist", "spotify:artist:artist1")),
            album = Album("album1", "Test Album", "https://album.image/large.jpg", "2023-01-01"),
            durationMs = 180000,
            popularity = 75,
            previewUrl = "https://preview.url/$id.mp3",
            spotifyUri = "spotify:track:$id",
            externalUrl = "https://open.spotify.com/track/$id"
        )
    }
}

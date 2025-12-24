package org.ilerna.song_swipe_frontend.presentation.screen.swipe

import org.ilerna.song_swipe_frontend.domain.model.Album
import org.ilerna.song_swipe_frontend.domain.model.Artist
import org.ilerna.song_swipe_frontend.domain.model.Track
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for SwipeState
 * Tests the computed properties in Success state
 */
class SwipeStateTest {

    @Test
    fun `currentTrack returns correct track at index`() {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"), createTrack("3"))
        val state = SwipeState.Success(tracks = tracks, currentIndex = 1)

        // Then
        assertEquals("2", state.currentTrack?.id)
    }

    @Test
    fun `currentTrack returns null when index out of bounds`() {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"))
        val state = SwipeState.Success(tracks = tracks, currentIndex = 5)

        // Then
        assertNull(state.currentTrack)
    }

    @Test
    fun `hasMoreTracks returns true when tracks remain`() {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"), createTrack("3"))
        val state = SwipeState.Success(tracks = tracks, currentIndex = 1)

        // Then
        assertTrue(state.hasMoreTracks)
    }

    @Test
    fun `hasMoreTracks returns false when all tracks viewed`() {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"))
        val state = SwipeState.Success(tracks = tracks, currentIndex = 2)

        // Then
        assertFalse(state.hasMoreTracks)
    }

    @Test
    fun `progress calculates correctly`() {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"), createTrack("3"), createTrack("4"))
        val state = SwipeState.Success(tracks = tracks, currentIndex = 2)

        // Then - 2/4 = 0.5
        assertEquals(0.5f, state.progress)
    }

    @Test
    fun `progress returns 0 for empty tracks`() {
        // Given
        val state = SwipeState.Success(tracks = emptyList(), currentIndex = 0)

        // Then
        assertEquals(0f, state.progress)
    }

    // ==================== Helper Functions ====================

    private fun createTrack(id: String): Track {
        return Track(
            id = id,
            name = "Track $id",
            artists = listOf(Artist("artist1", "Artist", "spotify:artist:1")),
            album = Album("album1", "Album", null, "2023"),
            durationMs = 180000,
            popularity = 75,
            previewUrl = null,
            spotifyUri = "spotify:track:$id",
            externalUrl = "https://open.spotify.com/track/$id"
        )
    }
}

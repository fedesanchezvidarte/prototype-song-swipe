package org.ilerna.song_swipe_frontend.presentation.screen.swipe

import org.ilerna.song_swipe_frontend.domain.model.Track

/**
 * Represents the UI state of the Swipe screen
 */
sealed class SwipeState {
    /**
     * Initial state before loading
     */
    data object Idle : SwipeState()

    /**
     * Loading tracks from the playlist
     */
    data object Loading : SwipeState()

    /**
     * Successfully loaded tracks, ready for swiping
     *
     * @param tracks List of tracks to swipe through
     * @param currentIndex Current track index being displayed
     * @param likedTracks List of track IDs the user liked
     * @param dislikedTracks List of track IDs the user disliked
     */
    data class Success(
        val tracks: List<Track>,
        val currentIndex: Int = 0,
        val likedTracks: List<String> = emptyList(),
        val dislikedTracks: List<String> = emptyList()
    ) : SwipeState() {
        val currentTrack: Track?
            get() = tracks.getOrNull(currentIndex)

        val hasMoreTracks: Boolean
            get() = currentIndex < tracks.size

        val progress: Float
            get() = if (tracks.isEmpty()) 0f else (currentIndex.toFloat() / tracks.size)
    }

    /**
     * Error state when loading fails
     *
     * @param message Error message to display
     */
    data class Error(val message: String) : SwipeState()
}

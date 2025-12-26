package org.ilerna.song_swipe_frontend.presentation.screen.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.usecase.playlist.GetPlaylistTracksUseCase
import javax.inject.Inject

/**
 * ViewModel for the Swipe screen
 * Manages track loading and swipe actions (like/dislike)
 */
@HiltViewModel
class SwipeViewModel @Inject constructor(
    private val getPlaylistTracksUseCase: GetPlaylistTracksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SwipeState>(SwipeState.Idle)
    val state: StateFlow<SwipeState> = _state.asStateFlow()

    /**
     * Load tracks from the playlist
     * Called when the screen is first displayed
     */
    fun loadTracks(playlistId: String? = null) {
        viewModelScope.launch {
            _state.value = SwipeState.Loading

            val result = if (playlistId != null) {
                getPlaylistTracksUseCase(playlistId)
            } else {
                getPlaylistTracksUseCase()
            }

            _state.value = when (result) {
                is NetworkResult.Success -> {
                    if (result.data.isEmpty()) {
                        SwipeState.Error("No tracks found in playlist")
                    } else {
                        SwipeState.Success(tracks = result.data)
                    }
                }

                is NetworkResult.Error -> {
                    SwipeState.Error(result.message)
                }

                is NetworkResult.Loading -> {
                    SwipeState.Loading
                }
            }
        }
    }

    /**
     * Like the current track and move to the next one
     */
    fun onLike() {
        val currentState = _state.value
        if (currentState is SwipeState.Success && currentState.currentTrack != null) {
            val likedTrackId = currentState.currentTrack!!.id
            _state.value = currentState.copy(
                currentIndex = currentState.currentIndex + 1,
                likedTracks = currentState.likedTracks + likedTrackId
            )
        }
    }

    /**
     * Dislike the current track and move to the next one
     */
    fun onDislike() {
        val currentState = _state.value
        if (currentState is SwipeState.Success && currentState.currentTrack != null) {
            val dislikedTrackId = currentState.currentTrack!!.id
            _state.value = currentState.copy(
                currentIndex = currentState.currentIndex + 1,
                dislikedTracks = currentState.dislikedTracks + dislikedTrackId
            )
        }
    }

    /**
     * Skip the current track without recording preference
     */
    fun onSkip() {
        val currentState = _state.value
        if (currentState is SwipeState.Success) {
            _state.value = currentState.copy(
                currentIndex = currentState.currentIndex + 1
            )
        }
    }

    /**
     * Reset to reload tracks
     */
    fun retry() {
        loadTracks()
    }
}

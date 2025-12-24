package org.ilerna.song_swipe_frontend.domain.usecase.playlist

import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.Track
import org.ilerna.song_swipe_frontend.domain.repository.PlaylistRepository

/**
 * Use case for fetching tracks from a Spotify playlist
 * Encapsulates the business logic for retrieving playlist tracks
 */
class GetPlaylistTracksUseCase(
    private val playlistRepository: PlaylistRepository
) {

    // TODO: Consider to refactor the Get Featured Playlists endpoint to fetch a random playlist each time
    companion object {
        /**
         * Default playlist ID - Rock Classics (Spotify curated playlist)
         * ~50 tracks with good mix of popular tracks with preview URLs
         */
        const val DEFAULT_PLAYLIST_ID = "37i9dQZF1DWXRqgorJj26U"
    }

    /**
     * Executes the use case to fetch tracks from a playlist
     *
     * @param playlistId Optional playlist ID, defaults to Rock Classics
     * @return NetworkResult containing list of Track data or error
     */
    suspend operator fun invoke(playlistId: String = DEFAULT_PLAYLIST_ID): NetworkResult<List<Track>> {
        return when (val result = playlistRepository.getPlaylist(playlistId)) {
            is NetworkResult.Success -> {
                NetworkResult.Success(result.data.tracks)
            }

            is NetworkResult.Error -> {
                NetworkResult.Error(result.message, result.code)
            }

            is NetworkResult.Loading -> {
                NetworkResult.Loading
            }
        }
    }
}

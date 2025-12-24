package org.ilerna.song_swipe_frontend.domain.repository

import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.Playlist

/**
 * Repository interface for Spotify Playlist operations
 * Handles playlist retrieval from Spotify Web API
 */
interface PlaylistRepository {

    /**
     * Gets a playlist by its Spotify ID
     * Returns the playlist with all its tracks
     *
     * @param playlistId The Spotify playlist ID
     * @return NetworkResult containing Playlist data or error
     */
    suspend fun getPlaylist(playlistId: String): NetworkResult<Playlist>
}

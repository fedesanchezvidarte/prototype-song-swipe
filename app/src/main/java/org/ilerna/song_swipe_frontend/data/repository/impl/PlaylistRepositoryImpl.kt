package org.ilerna.song_swipe_frontend.data.repository.impl

import org.ilerna.song_swipe_frontend.core.network.ApiResponse
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.data.datasource.remote.api.SpotifyApi
import org.ilerna.song_swipe_frontend.data.repository.mapper.PlaylistMapper
import org.ilerna.song_swipe_frontend.domain.model.Playlist
import org.ilerna.song_swipe_frontend.domain.repository.PlaylistRepository
import javax.inject.Inject

/**
 * Implementation of PlaylistRepository
 * Coordinates data from Spotify API and transforms it to domain models
 */
class PlaylistRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi
) : PlaylistRepository {

    /**
     * Gets a playlist by its Spotify ID
     * Converts ApiResponse to NetworkResult and maps DTO to domain model
     *
     * @param playlistId The Spotify playlist ID
     * @return NetworkResult containing Playlist or error
     */
    override suspend fun getPlaylist(playlistId: String): NetworkResult<Playlist> {
        return try {
            val response = spotifyApi.getPlaylist(playlistId)
            val apiResponse = ApiResponse.create(response)

            when (apiResponse) {
                is ApiResponse.Success -> {
                    try {
                        val playlist = PlaylistMapper.toDomain(apiResponse.data)
                        NetworkResult.Success(playlist)
                    } catch (e: Exception) {
                        NetworkResult.Error(
                            message = "Failed to process playlist: ${e.message}",
                            code = null
                        )
                    }
                }

                is ApiResponse.Error -> {
                    NetworkResult.Error(
                        message = apiResponse.message,
                        code = apiResponse.code
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Network error: ${e.message}",
                code = null
            )
        }
    }
}

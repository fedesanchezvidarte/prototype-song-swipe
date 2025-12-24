package org.ilerna.song_swipe_frontend.data.datasource.remote.api

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyUserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Spotify Web API interface for Retrofit
 * Base URL: https://api.spotify.com/
 */
interface SpotifyApi {

    /**
     * Get detailed profile information about the current user
     * Requires: user-read-email, user-read-private scopes
     *
     * @return Response containing the user's Spotify profile data
     */
    @GET("v1/me")
    suspend fun getCurrentUserProfile(): Response<SpotifyUserDto>

    /**
     * Get a playlist owned by a Spotify user
     * Returns full playlist object including tracks
     *
     * @param playlistId The Spotify ID of the playlist
     * @param market Optional ISO 3166-1 alpha-2 country code for track relinking
     * @param fields Optional comma-separated list of fields to filter response
     * @return Response containing the playlist data with tracks
     */
    @GET("v1/playlists/{playlist_id}")
    suspend fun getPlaylist(
        @Path("playlist_id") playlistId: String,
        @Query("market") market: String? = null,
        @Query("fields") fields: String? = null
    ): Response<SpotifyPlaylistDto>
}

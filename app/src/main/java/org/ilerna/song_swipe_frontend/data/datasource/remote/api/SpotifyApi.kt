package org.ilerna.song_swipe_frontend.data.datasource.remote.api

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoriesResponse
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoryDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoryPlaylistsResponse
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyFeaturedPlaylistsResponse
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

    /**
     * Get a list of categories used to tag items in Spotify
     *
     * @param locale The desired language (e.g., "es_ES", "en_US")
     * @param limit Maximum number of items to return (1-50, default 20)
     * @param offset Index of the first item to return (default 0)
     * @return Response containing paginated list of categories
     */
    @GET("v1/browse/categories")
    suspend fun getCategories(
        @Query("locale") locale: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<SpotifyCategoriesResponse>

    /**
     * Get a single category used to tag items in Spotify
     *
     * @param categoryId The Spotify category ID (e.g., "dinner", "pop")
     * @param locale The desired language (e.g., "es_ES", "en_US")
     * @return Response containing the category data
     */
    @GET("v1/browse/categories/{category_id}")
    suspend fun getCategory(
        @Path("category_id") categoryId: String,
        @Query("locale") locale: String? = null
    ): Response<SpotifyCategoryDto>

    /**
     * Get a list of Spotify playlists tagged with a particular category
     *
     * @param categoryId The Spotify category ID (e.g., "dinner", "pop")
     * @param limit Maximum number of items to return (1-50, default 20)
     * @param offset Index of the first item to return (default 0)
     * @return Response containing paginated list of playlists for the category
     */
    @GET("v1/browse/categories/{category_id}/playlists")
    suspend fun getCategoryPlaylists(
        @Path("category_id") categoryId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<SpotifyCategoryPlaylistsResponse>

    /**
     * Get a list of Spotify featured playlists
     * Note: This endpoint is deprecated but still functional
     *
     * @param locale The desired language (e.g., "es_ES", "en_US")
     * @param limit Maximum number of items to return (1-50, default 20)
     * @param offset Index of the first item to return (default 0)
     * @return Response containing featured playlists with optional message
     */
    @GET("v1/browse/featured-playlists")
    suspend fun getFeaturedPlaylists(
        @Query("locale") locale: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<SpotifyFeaturedPlaylistsResponse>
}

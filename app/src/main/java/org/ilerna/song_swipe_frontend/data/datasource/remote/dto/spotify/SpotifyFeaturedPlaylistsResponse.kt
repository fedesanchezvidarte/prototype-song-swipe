package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for the response of GET /browse/featured-playlists endpoint
 * Contains a message and paginated list of featured playlists
 * Note: This endpoint is deprecated but still functional
 */
data class SpotifyFeaturedPlaylistsResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("playlists")
    val playlists: SpotifyPaginatedPlaylistsDto
)

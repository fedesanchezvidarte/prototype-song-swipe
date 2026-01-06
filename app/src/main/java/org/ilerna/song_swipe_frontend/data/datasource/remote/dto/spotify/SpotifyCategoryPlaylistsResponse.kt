package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for the response of GET /browse/categories/{category_id}/playlists endpoint
 * Contains a paginated list of playlists for a specific category
 */
data class SpotifyCategoryPlaylistsResponse(
    @SerializedName("message")
    val message: String?,

    @SerializedName("playlists")
    val playlists: SpotifyPaginatedPlaylistsDto
)

/**
 * DTO for paginated playlists list
 * Contains the items array and pagination metadata
 */
data class SpotifyPaginatedPlaylistsDto(
    @SerializedName("items")
    val items: List<SpotifySimplifiedPlaylistDto?>,

    @SerializedName("href")
    val href: String?,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?
)

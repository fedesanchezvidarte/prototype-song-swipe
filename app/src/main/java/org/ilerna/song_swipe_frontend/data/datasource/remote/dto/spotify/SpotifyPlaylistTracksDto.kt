package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify playlist tracks paging object
 * Contains a page of tracks from a playlist with pagination information
 */
data class SpotifyPlaylistTracksDto(
    @SerializedName("items")
    val items: List<SpotifyPlaylistTrackDto>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?,

    @SerializedName("href")
    val href: String?
)

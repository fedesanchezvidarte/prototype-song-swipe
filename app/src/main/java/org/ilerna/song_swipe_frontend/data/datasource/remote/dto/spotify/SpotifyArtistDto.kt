package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify artist object (simplified version for track responses)
 * Contains basic artist information
 */
data class SpotifyArtistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("uri")
    val uri: String,

    @SerializedName("href")
    val href: String?,

    @SerializedName("external_urls")
    val externalUrls: SpotifyExternalUrlsDto?
)

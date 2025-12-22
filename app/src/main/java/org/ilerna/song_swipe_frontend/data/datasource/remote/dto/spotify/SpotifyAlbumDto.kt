package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify album object (simplified version for track responses)
 * Contains essential album information including artwork
 */
data class SpotifyAlbumDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("images")
    val images: List<SpotifyImageDto>,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("album_type")
    val albumType: String?,

    @SerializedName("uri")
    val uri: String?,

    @SerializedName("external_urls")
    val externalUrls: SpotifyExternalUrlsDto?
)

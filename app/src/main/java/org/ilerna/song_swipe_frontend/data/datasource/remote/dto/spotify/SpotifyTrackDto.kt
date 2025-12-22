package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify track object
 * Contains complete track information including artists, album, and preview URL
 */
data class SpotifyTrackDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("duration_ms")
    val durationMs: Int,

    @SerializedName("popularity")
    val popularity: Int,

    @SerializedName("preview_url")
    val previewUrl: String?,

    @SerializedName("artists")
    val artists: List<SpotifyArtistDto>,

    @SerializedName("album")
    val album: SpotifyAlbumDto,

    @SerializedName("uri")
    val uri: String,

    @SerializedName("external_urls")
    val externalUrls: SpotifyExternalUrlsDto,

    @SerializedName("explicit")
    val explicit: Boolean?,

    @SerializedName("href")
    val href: String?,

    @SerializedName("is_local")
    val isLocal: Boolean?,

    @SerializedName("track_number")
    val trackNumber: Int?
)

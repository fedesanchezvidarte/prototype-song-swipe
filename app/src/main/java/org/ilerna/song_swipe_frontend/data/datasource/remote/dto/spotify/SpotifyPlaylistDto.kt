package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify playlist object
 * Contains complete playlist information including metadata and tracks
 */
data class SpotifyPlaylistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("images")
    val images: List<SpotifyImageDto>?,

    @SerializedName("tracks")
    val tracks: SpotifyPlaylistTracksDto,

    @SerializedName("owner")
    val owner: SpotifyPlaylistOwnerDto?,

    @SerializedName("public")
    val public: Boolean?,

    @SerializedName("collaborative")
    val collaborative: Boolean?,

    @SerializedName("snapshot_id")
    val snapshotId: String?,

    @SerializedName("uri")
    val uri: String?,

    @SerializedName("external_urls")
    val externalUrls: SpotifyExternalUrlsDto?,

    @SerializedName("href")
    val href: String?
)

/**
 * DTO for Spotify playlist owner
 */
data class SpotifyPlaylistOwnerDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("display_name")
    val displayName: String?,

    @SerializedName("uri")
    val uri: String?,

    @SerializedName("href")
    val href: String?,

    @SerializedName("external_urls")
    val externalUrls: SpotifyExternalUrlsDto?
)

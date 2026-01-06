package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify simplified playlist object
 * Used in category playlists and featured playlists responses
 * Contains less detail than full SpotifyPlaylistDto (no tracks)
 */
data class SpotifySimplifiedPlaylistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("images")
    val images: List<SpotifyImageDto>?,

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
    val href: String?,

    @SerializedName("tracks")
    val tracks: SpotifyPlaylistTracksRefDto?
)

/**
 * DTO for playlist tracks reference (used in simplified playlist)
 * Contains only href and total count, not the actual tracks
 */
data class SpotifyPlaylistTracksRefDto(
    @SerializedName("href")
    val href: String?,

    @SerializedName("total")
    val total: Int
)

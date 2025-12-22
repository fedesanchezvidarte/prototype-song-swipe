package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify playlist track item wrapper
 * Wraps a track object with additional playlist-specific metadata
 */
data class SpotifyPlaylistTrackDto(
    @SerializedName("added_at")
    val addedAt: String?,

    @SerializedName("added_by")
    val addedBy: SpotifyAddedByDto?,

    @SerializedName("is_local")
    val isLocal: Boolean?,

    @SerializedName("track")
    val track: SpotifyTrackDto?
)

/**
 * DTO for the user who added a track to the playlist
 */
data class SpotifyAddedByDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("uri")
    val uri: String?,

    @SerializedName("href")
    val href: String?
)

package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for Spotify external URLs object
 * Contains external URLs for a Spotify resource (track, album, artist, etc.)
 */
data class SpotifyExternalUrlsDto(
    @SerializedName("spotify")
    val spotify: String
)

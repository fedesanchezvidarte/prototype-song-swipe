package org.ilerna.song_swipe_frontend.domain.model

/**
 * Domain model representing a music track
 */
data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val durationMs: Int,
    val popularity: Int,
    val previewUrl: String?,
    val spotifyUri: String,
    val externalUrl: String
)

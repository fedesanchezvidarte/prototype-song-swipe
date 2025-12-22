package org.ilerna.song_swipe_frontend.domain.model

/**
 * Domain model representing a Spotify playlist
 */
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val tracks: List<Track>,
    val totalTracks: Int
)

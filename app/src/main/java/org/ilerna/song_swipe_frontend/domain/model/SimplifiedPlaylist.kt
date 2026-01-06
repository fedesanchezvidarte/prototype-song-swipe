package org.ilerna.song_swipe_frontend.domain.model

/**
 * Domain model representing a simplified playlist.
 * Used for category playlists and featured playlists where
 * full track data is not needed (only metadata for selection).
 *
 * @param id Spotify playlist ID
 * @param name Display name of the playlist
 * @param description Optional description of the playlist
 * @param imageUrl URL of the playlist cover image
 * @param ownerName Name of the playlist owner
 * @param totalTracks Total number of tracks in the playlist
 */
data class SimplifiedPlaylist(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val ownerName: String?,
    val totalTracks: Int
)

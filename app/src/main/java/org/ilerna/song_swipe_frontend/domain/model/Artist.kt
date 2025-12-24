package org.ilerna.song_swipe_frontend.domain.model

/**
 * Domain model representing an artist
 */
data class Artist(
    val id: String,
    val name: String,
    val spotifyUri: String
)

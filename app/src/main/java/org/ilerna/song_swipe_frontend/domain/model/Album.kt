package org.ilerna.song_swipe_frontend.domain.model

/**
 * Domain model representing an album
 */
data class Album(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val releaseDate: String
)

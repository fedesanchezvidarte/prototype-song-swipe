package org.ilerna.song_swipe_frontend.domain.model

/**
 * Domain model representing a music category/genre.
 * Used to display category cards on the Home screen.
 * 
 * Note: Colors are mapped in the presentation layer based on ID,
 * keeping this domain model platform-agnostic.
 *
 * @param id Unique identifier for the category (used for color mapping and API calls)
 * @param name Display name of the category
 * @param iconUrl URL of the category icon from Spotify API (nullable for local categories)
 */
data class MusicCategory(
    val id: String,
    val name: String,
    val iconUrl: String? = null
)

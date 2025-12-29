package org.ilerna.song_swipe_frontend.domain.repository

import org.ilerna.song_swipe_frontend.domain.model.MusicCategory

/**
 * Repository interface for music category operations.
 * Provides access to available music genres/categories.
 */
interface CategoryRepository {
    
    /**
     * Get all available music categories.
     * @return List of music categories
     */
    suspend fun getCategories(): Result<List<MusicCategory>>
    
    /**
     * Get a specific category by ID.
     * @param id The category ID
     * @return The music category or null if not found
     */
    suspend fun getCategoryById(id: String): Result<MusicCategory?>
}

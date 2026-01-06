package org.ilerna.song_swipe_frontend.domain.repository

import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.MusicCategory
import org.ilerna.song_swipe_frontend.domain.model.SimplifiedPlaylist

/**
 * Repository interface for music category operations.
 * Provides access to available music genres/categories.
 */
interface CategoryRepository {
    
    /**
     * Get all available music categories from local/mock data.
     * Used as fallback when API is unavailable.
     * @return List of music categories
     */
    suspend fun getCategories(): Result<List<MusicCategory>>
    
    /**
     * Get a specific category by ID from local/mock data.
     * @param id The category ID
     * @return The music category or null if not found
     */
    suspend fun getCategoryById(id: String): Result<MusicCategory?>

    /**
     * Get all available music categories from Spotify API.
     * @param locale Optional language code (e.g., "es_ES", "en_US")
     * @param limit Maximum number of categories to return (1-50)
     * @return NetworkResult containing list of categories or error
     */
    suspend fun getCategoriesFromApi(
        locale: String? = null,
        limit: Int = 20
    ): NetworkResult<List<MusicCategory>>

    /**
     * Get playlists for a specific category from Spotify API.
     * @param categoryId The Spotify category ID (e.g., "pop", "rock")
     * @param limit Maximum number of playlists to return (1-50)
     * @return NetworkResult containing list of simplified playlists or error
     */
    suspend fun getCategoryPlaylists(
        categoryId: String,
        limit: Int = 20
    ): NetworkResult<List<SimplifiedPlaylist>>

    /**
     * Get featured playlists from Spotify API.
     * Note: This endpoint is deprecated but still functional.
     * @param locale Optional language code (e.g., "es_ES", "en_US")
     * @param limit Maximum number of playlists to return (1-50)
     * @return NetworkResult containing list of simplified playlists or error
     */
    suspend fun getFeaturedPlaylists(
        locale: String? = null,
        limit: Int = 20
    ): NetworkResult<List<SimplifiedPlaylist>>
}

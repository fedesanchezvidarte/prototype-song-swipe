package org.ilerna.song_swipe_frontend.data.repository.impl

import org.ilerna.song_swipe_frontend.domain.model.MusicCategory
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CategoryRepository.
 * Currently provides mock data for the prototype.
 * Will be connected to remote data source in the future.
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor() : CategoryRepository {
    
    /**
     * Mock categories for the prototype.
     * Colors are mapped in the presentation layer based on ID.
     */
    private val mockCategories = listOf(
        MusicCategory("1", "Pop"),
        MusicCategory("2", "Rock"),
        MusicCategory("3", "Electronic"),
        MusicCategory("4", "Hip Hop"),
        MusicCategory("5", "Jazz"),
        MusicCategory("6", "Classical"),
        MusicCategory("7", "R&B"),
        MusicCategory("8", "Country"),
        MusicCategory("9", "Latin"),
        MusicCategory("10", "Indie")
    )
    
    override suspend fun getCategories(): Result<List<MusicCategory>> {
        return Result.success(mockCategories)
    }
    
    override suspend fun getCategoryById(id: String): Result<MusicCategory?> {
        return Result.success(mockCategories.find { it.id == id })
    }
}

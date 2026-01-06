package org.ilerna.song_swipe_frontend.data.repository.impl

import org.ilerna.song_swipe_frontend.core.network.ApiResponse
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.data.datasource.remote.api.SpotifyApi
import org.ilerna.song_swipe_frontend.data.repository.mapper.CategoryMapper
import org.ilerna.song_swipe_frontend.data.repository.mapper.SimplifiedPlaylistMapper
import org.ilerna.song_swipe_frontend.domain.model.MusicCategory
import org.ilerna.song_swipe_frontend.domain.model.SimplifiedPlaylist
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CategoryRepository.
 * Provides both mock data (for offline/fallback) and Spotify API integration.
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi
) : CategoryRepository {
    
    /**
     * Mock categories for the prototype and offline fallback.
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

    override suspend fun getCategoriesFromApi(
        locale: String?,
        limit: Int
    ): NetworkResult<List<MusicCategory>> {
        return try {
            val response = spotifyApi.getCategories(locale = locale, limit = limit)
            val apiResponse = ApiResponse.create(response)

            when (apiResponse) {
                is ApiResponse.Success -> {
                    try {
                        val categories = CategoryMapper.toDomainList(
                            apiResponse.data.categories.items
                        )
                        NetworkResult.Success(categories)
                    } catch (e: Exception) {
                        NetworkResult.Error(
                            message = "Failed to process categories: ${e.message}",
                            code = null
                        )
                    }
                }

                is ApiResponse.Error -> {
                    NetworkResult.Error(
                        message = apiResponse.message,
                        code = apiResponse.code
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Network error: ${e.message}",
                code = null
            )
        }
    }

    override suspend fun getCategoryPlaylists(
        categoryId: String,
        limit: Int
    ): NetworkResult<List<SimplifiedPlaylist>> {
        return try {
            val response = spotifyApi.getCategoryPlaylists(categoryId = categoryId, limit = limit)
            val apiResponse = ApiResponse.create(response)

            when (apiResponse) {
                is ApiResponse.Success -> {
                    try {
                        val playlists = SimplifiedPlaylistMapper.toDomainList(
                            apiResponse.data.playlists.items
                        )
                        NetworkResult.Success(playlists)
                    } catch (e: Exception) {
                        NetworkResult.Error(
                            message = "Failed to process category playlists: ${e.message}",
                            code = null
                        )
                    }
                }

                is ApiResponse.Error -> {
                    NetworkResult.Error(
                        message = apiResponse.message,
                        code = apiResponse.code
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Network error: ${e.message}",
                code = null
            )
        }
    }

    override suspend fun getFeaturedPlaylists(
        locale: String?,
        limit: Int
    ): NetworkResult<List<SimplifiedPlaylist>> {
        return try {
            val response = spotifyApi.getFeaturedPlaylists(locale = locale, limit = limit)
            val apiResponse = ApiResponse.create(response)

            when (apiResponse) {
                is ApiResponse.Success -> {
                    try {
                        val playlists = SimplifiedPlaylistMapper.toDomainList(
                            apiResponse.data.playlists.items
                        )
                        NetworkResult.Success(playlists)
                    } catch (e: Exception) {
                        NetworkResult.Error(
                            message = "Failed to process featured playlists: ${e.message}",
                            code = null
                        )
                    }
                }

                is ApiResponse.Error -> {
                    NetworkResult.Error(
                        message = apiResponse.message,
                        code = apiResponse.code
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Network error: ${e.message}",
                code = null
            )
        }
    }
}

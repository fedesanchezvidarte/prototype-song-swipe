package org.ilerna.song_swipe_frontend.domain.usecase.category

import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.SimplifiedPlaylist
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case for fetching playlists from a Spotify category.
 * Encapsulates the business logic for retrieving category playlists
 * and optionally selecting a random playlist.
 */
class GetCategoryPlaylistsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    /**
     * Executes the use case to fetch playlists for a category.
     *
     * @param categoryId The Spotify category ID (e.g., "pop", "rock")
     * @param limit Maximum number of playlists to fetch (1-50)
     * @return NetworkResult containing list of SimplifiedPlaylist or error
     */
    suspend operator fun invoke(
        categoryId: String,
        limit: Int = 20
    ): NetworkResult<List<SimplifiedPlaylist>> {
        return categoryRepository.getCategoryPlaylists(categoryId, limit)
    }

    /**
     * Fetches playlists for a category and returns a random one.
     * Useful for auto-selecting a playlist when user clicks a category.
     *
     * @param categoryId The Spotify category ID (e.g., "pop", "rock")
     * @param limit Maximum number of playlists to fetch before random selection
     * @return NetworkResult containing a random SimplifiedPlaylist or error
     */
    suspend fun getRandomPlaylist(
        categoryId: String,
        limit: Int = 20
    ): NetworkResult<SimplifiedPlaylist> {
        return when (val result = categoryRepository.getCategoryPlaylists(categoryId, limit)) {
            is NetworkResult.Success -> {
                val playlists = result.data
                if (playlists.isEmpty()) {
                    NetworkResult.Error("No playlists found for this category")
                } else {
                    NetworkResult.Success(playlists.random())
                }
            }
            is NetworkResult.Error -> {
                NetworkResult.Error(result.message, result.code)
            }
            is NetworkResult.Loading -> {
                NetworkResult.Loading
            }
        }
    }
}

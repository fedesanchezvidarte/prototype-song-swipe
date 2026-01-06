package org.ilerna.song_swipe_frontend.domain.usecase.category

import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.SimplifiedPlaylist
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case for fetching featured playlists from Spotify.
 * Encapsulates the business logic for retrieving featured playlists
 * and optionally selecting a random playlist for "Discover" functionality.
 * 
 * Note: The featured playlists endpoint is deprecated but still functional.
 */
class GetFeaturedPlaylistsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    /**
     * Executes the use case to fetch featured playlists.
     *
     * @param locale Optional language code (e.g., "es_ES", "en_US")
     * @param limit Maximum number of playlists to fetch (1-50)
     * @return NetworkResult containing list of SimplifiedPlaylist or error
     */
    suspend operator fun invoke(
        locale: String? = null,
        limit: Int = 20
    ): NetworkResult<List<SimplifiedPlaylist>> {
        return categoryRepository.getFeaturedPlaylists(locale, limit)
    }

    /**
     * Fetches featured playlists and returns a random one.
     * Useful for "Discover" or "Swipe" functionality where user wants
     * to explore music without selecting a specific category.
     *
     * @param locale Optional language code (e.g., "es_ES", "en_US")
     * @param limit Maximum number of playlists to fetch before random selection
     * @return NetworkResult containing a random SimplifiedPlaylist or error
     */
    suspend fun getRandomPlaylist(
        locale: String? = null,
        limit: Int = 20
    ): NetworkResult<SimplifiedPlaylist> {
        return when (val result = categoryRepository.getFeaturedPlaylists(locale, limit)) {
            is NetworkResult.Success -> {
                val playlists = result.data
                if (playlists.isEmpty()) {
                    NetworkResult.Error("No featured playlists available")
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

package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifySimplifiedPlaylistDto
import org.ilerna.song_swipe_frontend.domain.model.SimplifiedPlaylist

/**
 * Mapper to convert Spotify Simplified Playlist DTOs to domain models
 */
object SimplifiedPlaylistMapper {

    /**
     * Converts SpotifySimplifiedPlaylistDto to SimplifiedPlaylist domain model
     *
     * @param dto The Spotify simplified playlist DTO from the API
     * @return SimplifiedPlaylist domain model
     */
    fun toDomain(dto: SpotifySimplifiedPlaylistDto): SimplifiedPlaylist {
        return SimplifiedPlaylist(
            id = dto.id,
            name = dto.name,
            description = dto.description?.takeIf { it.isNotBlank() },
            imageUrl = selectBestImage(dto.images),
            ownerName = dto.owner?.displayName,
            totalTracks = dto.tracks?.total ?: 0
        )
    }

    /**
     * Converts a list of SpotifySimplifiedPlaylistDto to a list of SimplifiedPlaylist domain models
     * Filters out null items that may appear in paginated responses
     *
     * @param dtos List of Spotify simplified playlist DTOs (may contain nulls)
     * @return List of SimplifiedPlaylist domain models
     */
    fun toDomainList(dtos: List<SpotifySimplifiedPlaylistDto?>): List<SimplifiedPlaylist> {
        return dtos.filterNotNull().map { toDomain(it) }
    }

    /**
     * Selects the best image from a list of Spotify images
     * Prefers the first (typically largest) image for better quality
     *
     * @param images List of Spotify images (nullable)
     * @return URL of the selected image, or null if no images available
     */
    private fun selectBestImage(images: List<SpotifyImageDto>?): String? {
        if (images.isNullOrEmpty()) return null
        return images.firstOrNull()?.url
    }
}

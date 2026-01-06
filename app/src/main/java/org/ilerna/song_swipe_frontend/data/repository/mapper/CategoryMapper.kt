package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoryDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.domain.model.MusicCategory

/**
 * Mapper to convert Spotify Category DTOs to domain models
 */
object CategoryMapper {

    /**
     * Converts SpotifyCategoryDto to MusicCategory domain model
     *
     * @param dto The Spotify category DTO from the API
     * @return MusicCategory domain model
     */
    fun toDomain(dto: SpotifyCategoryDto): MusicCategory {
        return MusicCategory(
            id = dto.id,
            name = dto.name,
            iconUrl = selectBestIcon(dto.icons)
        )
    }

    /**
     * Converts a list of SpotifyCategoryDto to a list of MusicCategory domain models
     *
     * @param dtos List of Spotify category DTOs
     * @return List of MusicCategory domain models
     */
    fun toDomainList(dtos: List<SpotifyCategoryDto>): List<MusicCategory> {
        return dtos.map { toDomain(it) }
    }

    /**
     * Selects the best icon from a list of Spotify images
     * Prefers the first (typically largest) image
     *
     * @param icons List of Spotify images (nullable)
     * @return URL of the selected icon, or null if no icons available
     */
    private fun selectBestIcon(icons: List<SpotifyImageDto>?): String? {
        if (icons.isNullOrEmpty()) return null
        return icons.firstOrNull()?.url
    }
}

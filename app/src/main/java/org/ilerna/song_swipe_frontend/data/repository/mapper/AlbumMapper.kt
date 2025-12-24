package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyAlbumDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.domain.model.Album

/**
 * Mapper to convert Spotify Album DTOs to domain models
 */
object AlbumMapper {

    /**
     * Converts SpotifyAlbumDto to Album domain model
     * Selects the largest available album artwork if multiple are present
     *
     * @param dto The Spotify album DTO from the API
     * @return Album domain model
     */
    fun toDomain(dto: SpotifyAlbumDto): Album {
        return Album(
            id = dto.id,
            name = dto.name,
            imageUrl = selectBestImage(dto.images),
            releaseDate = dto.releaseDate
        )
    }

    /**
     * Selects the best image from a list of Spotify images
     * Prefers larger images for better quality
     * Spotify returns images sorted by size (largest first)
     *
     * @param images List of Spotify images
     * @return URL of the selected image, or null if no images available
     */
    private fun selectBestImage(images: List<SpotifyImageDto>): String? {
        if (images.isEmpty()) return null
        
        // Return the first image (largest)
        return images.firstOrNull()?.url
    }
}

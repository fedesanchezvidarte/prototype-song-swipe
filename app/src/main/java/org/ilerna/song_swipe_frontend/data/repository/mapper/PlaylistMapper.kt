package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistDto
import org.ilerna.song_swipe_frontend.domain.model.Playlist

/**
 * Mapper to convert Spotify Playlist DTOs to domain models
 */
object PlaylistMapper {

    /**
     * Converts SpotifyPlaylistDto to Playlist domain model
     * Filters out null tracks (can occur with local files or unavailable tracks)
     *
     * @param dto The Spotify playlist DTO from the API
     * @return Playlist domain model
     */
    fun toDomain(dto: SpotifyPlaylistDto): Playlist {
        return Playlist(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            imageUrl = selectBestImage(dto.images),
            tracks = dto.tracks.items
                .mapNotNull { it.track }  // Filter out null tracks
                .filter { !it.isLocal.orFalse() }  // Filter out local files
                .map { TrackMapper.toDomain(it) },
            totalTracks = dto.tracks.total
        )
    }

    /**
     * Selects the best image from a list of Spotify images
     * Prefers larger images for better quality
     *
     * @param images List of Spotify images (nullable)
     * @return URL of the selected image, or null if no images available
     */
    private fun selectBestImage(images: List<SpotifyImageDto>?): String? {
        if (images.isNullOrEmpty()) return null
        return images.firstOrNull()?.url
    }

    /**
     * Extension function to handle nullable Boolean
     */
    private fun Boolean?.orFalse(): Boolean = this ?: false
}

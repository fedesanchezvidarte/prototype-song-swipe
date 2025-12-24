package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyArtistDto
import org.ilerna.song_swipe_frontend.domain.model.Artist

/**
 * Mapper to convert Spotify Artist DTOs to domain models
 */
object ArtistMapper {

    /**
     * Converts SpotifyArtistDto to Artist domain model
     *
     * @param dto The Spotify artist DTO from the API
     * @return Artist domain model
     */
    fun toDomain(dto: SpotifyArtistDto): Artist {
        return Artist(
            id = dto.id,
            name = dto.name,
            spotifyUri = dto.uri
        )
    }
}

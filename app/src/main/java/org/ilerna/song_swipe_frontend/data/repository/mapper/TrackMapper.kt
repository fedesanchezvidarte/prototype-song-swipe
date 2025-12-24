package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyTrackDto
import org.ilerna.song_swipe_frontend.domain.model.Track

/**
 * Mapper to convert Spotify Track DTOs to domain models
 */
object TrackMapper {

    /**
     * Converts SpotifyTrackDto to Track domain model
     *
     * @param dto The Spotify track DTO from the API
     * @return Track domain model
     */
    fun toDomain(dto: SpotifyTrackDto): Track {
        return Track(
            id = dto.id,
            name = dto.name,
            artists = dto.artists.map { ArtistMapper.toDomain(it) },
            album = AlbumMapper.toDomain(dto.album),
            durationMs = dto.durationMs,
            popularity = dto.popularity,
            previewUrl = dto.previewUrl,
            spotifyUri = dto.uri,
            externalUrl = dto.externalUrls.spotify
        )
    }
}

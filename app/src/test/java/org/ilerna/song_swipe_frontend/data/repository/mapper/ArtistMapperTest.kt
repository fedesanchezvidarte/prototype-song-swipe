package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyArtistDto
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for ArtistMapper
 * Focuses on DTO to domain model transformation
 */
class ArtistMapperTest {

    @Test
    fun `toDomain should map all fields correctly`() {
        // Given
        val dto = SpotifyArtistDto(
            id = "artist123",
            name = "Test Artist",
            uri = "spotify:artist:artist123",
            href = "https://api.spotify.com/v1/artists/artist123",
            externalUrls = null
        )

        // When
        val artist = ArtistMapper.toDomain(dto)

        // Then
        assertEquals("artist123", artist.id)
        assertEquals("Test Artist", artist.name)
        assertEquals("spotify:artist:artist123", artist.spotifyUri)
    }
}

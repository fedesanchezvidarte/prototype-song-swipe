package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyAlbumDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for AlbumMapper
 * Focuses on DTO to domain model transformation and image selection logic
 */
class AlbumMapperTest {

    @Test
    fun `toDomain should map all fields correctly`() {
        // Given
        val dto = SpotifyAlbumDto(
            id = "album123",
            name = "Test Album",
            images = listOf(
                SpotifyImageDto(url = "https://example.com/large.jpg", height = 640, width = 640),
                SpotifyImageDto(url = "https://example.com/medium.jpg", height = 300, width = 300)
            ),
            releaseDate = "2023-05-15",
            albumType = "album",
            uri = "spotify:album:album123",
            externalUrls = null
        )

        // When
        val album = AlbumMapper.toDomain(dto)

        // Then
        assertEquals("album123", album.id)
        assertEquals("Test Album", album.name)
        assertEquals("2023-05-15", album.releaseDate)
        assertEquals("https://example.com/large.jpg", album.imageUrl) // Should select largest
    }

    @Test
    fun `toDomain should return null imageUrl when images list is empty`() {
        // Given
        val dto = SpotifyAlbumDto(
            id = "album456",
            name = "No Cover Album",
            images = emptyList(),
            releaseDate = "2020-01-01",
            albumType = "single",
            uri = null,
            externalUrls = null
        )

        // When
        val album = AlbumMapper.toDomain(dto)

        // Then
        assertNull(album.imageUrl)
    }
}

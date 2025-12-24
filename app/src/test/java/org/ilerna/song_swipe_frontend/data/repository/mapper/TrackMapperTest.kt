package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyAlbumDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyArtistDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyExternalUrlsDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyTrackDto
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for TrackMapper
 * Focuses on DTO to domain model transformation
 */
class TrackMapperTest {

    @Test
    fun `toDomain should map all fields correctly`() {
        // Given
        val dto = createTrackDto(
            id = "track123",
            name = "Test Track",
            previewUrl = "https://preview.url/track.mp3"
        )

        // When
        val track = TrackMapper.toDomain(dto)

        // Then
        assertEquals("track123", track.id)
        assertEquals("Test Track", track.name)
        assertEquals(240000, track.durationMs)
        assertEquals(85, track.popularity)
        assertEquals("https://preview.url/track.mp3", track.previewUrl)
        assertEquals("spotify:track:track123", track.spotifyUri)
        assertEquals("https://open.spotify.com/track/track123", track.externalUrl)
    }

    @Test
    fun `toDomain should map artists correctly`() {
        // Given
        val dto = createTrackDto(artistCount = 2)

        // When
        val track = TrackMapper.toDomain(dto)

        // Then
        assertEquals(2, track.artists.size)
        assertEquals("Artist 1", track.artists[0].name)
        assertEquals("Artist 2", track.artists[1].name)
    }

    @Test
    fun `toDomain should handle null previewUrl`() {
        // Given
        val dto = createTrackDto(previewUrl = null)

        // When
        val track = TrackMapper.toDomain(dto)

        // Then
        assertEquals(null, track.previewUrl)
    }

    // ==================== Helper Functions ====================

    private fun createTrackDto(
        id: String = "track123",
        name: String = "Test Track",
        previewUrl: String? = "https://preview.url/track.mp3",
        artistCount: Int = 1
    ): SpotifyTrackDto {
        val artists = (1..artistCount).map { index ->
            SpotifyArtistDto(
                id = "artist$index",
                name = "Artist $index",
                uri = "spotify:artist:artist$index",
                href = null,
                externalUrls = null
            )
        }

        return SpotifyTrackDto(
            id = id,
            name = name,
            durationMs = 240000,
            popularity = 85,
            previewUrl = previewUrl,
            artists = artists,
            album = SpotifyAlbumDto(
                id = "album123",
                name = "Test Album",
                images = listOf(SpotifyImageDto("https://album.image/large.jpg", 640, 640)),
                releaseDate = "2023-01-15",
                albumType = "album",
                uri = null,
                externalUrls = null
            ),
            uri = "spotify:track:$id",
            externalUrls = SpotifyExternalUrlsDto("https://open.spotify.com/track/$id"),
            explicit = false,
            href = null,
            isLocal = false,
            trackNumber = 1
        )
    }
}

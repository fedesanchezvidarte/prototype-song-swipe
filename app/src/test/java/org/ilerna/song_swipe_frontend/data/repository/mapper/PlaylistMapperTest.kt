package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyAlbumDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyArtistDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyExternalUrlsDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistTrackDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistTracksDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyTrackDto
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for PlaylistMapper
 * Focuses on DTO to domain model transformation and track filtering
 */
class PlaylistMapperTest {

    @Test
    fun `toDomain should map all fields correctly`() {
        // Given
        val dto = createPlaylistDto(
            id = "playlist123",
            name = "Test Playlist",
            description = "A test playlist"
        )

        // When
        val playlist = PlaylistMapper.toDomain(dto)

        // Then
        assertEquals("playlist123", playlist.id)
        assertEquals("Test Playlist", playlist.name)
        assertEquals("A test playlist", playlist.description)
        assertEquals("https://playlist.image/large.jpg", playlist.imageUrl)
        assertEquals(2, playlist.totalTracks)
    }

    @Test
    fun `toDomain should filter out null tracks`() {
        // Given - Playlist with one valid track and one null track
        val dto = createPlaylistDto(includeNullTrack = true)

        // When
        val playlist = PlaylistMapper.toDomain(dto)

        // Then - Only valid tracks should be included
        assertEquals(1, playlist.tracks.size)
        assertEquals("track1", playlist.tracks[0].id)
    }

    @Test
    fun `toDomain should filter out local tracks`() {
        // Given - Playlist with one regular track and one local track
        val dto = createPlaylistDto(includeLocalTrack = true)

        // When
        val playlist = PlaylistMapper.toDomain(dto)

        // Then - Local tracks should be filtered out
        assertEquals(1, playlist.tracks.size)
        assertEquals("track1", playlist.tracks[0].id)
    }

    @Test
    fun `toDomain should handle null images`() {
        // Given
        val dto = createPlaylistDto().copy(images = null)

        // When
        val playlist = PlaylistMapper.toDomain(dto)

        // Then
        assertNull(playlist.imageUrl)
    }

    // ==================== Helper Functions ====================

    private fun createPlaylistDto(
        id: String = "playlist123",
        name: String = "Test Playlist",
        description: String? = "A test playlist",
        includeNullTrack: Boolean = false,
        includeLocalTrack: Boolean = false
    ): SpotifyPlaylistDto {
        val trackItems = mutableListOf<SpotifyPlaylistTrackDto>()

        // Add valid track
        trackItems.add(
            SpotifyPlaylistTrackDto(
                addedAt = null,
                addedBy = null,
                isLocal = false,
                track = createTrackDto("track1", "Track 1", isLocal = false)
            )
        )

        // Add second valid track or special case
        when {
            includeNullTrack -> trackItems.add(
                SpotifyPlaylistTrackDto(
                    addedAt = null,
                    addedBy = null,
                    isLocal = false,
                    track = null
                )
            )

            includeLocalTrack -> trackItems.add(
                SpotifyPlaylistTrackDto(
                    addedAt = null,
                    addedBy = null,
                    isLocal = true,
                    track = createTrackDto("localTrack", "Local Track", isLocal = true)
                )
            )

            else -> trackItems.add(
                SpotifyPlaylistTrackDto(
                    addedAt = null,
                    addedBy = null,
                    isLocal = false,
                    track = createTrackDto("track2", "Track 2", isLocal = false)
                )
            )
        }

        return SpotifyPlaylistDto(
            id = id,
            name = name,
            description = description,
            images = listOf(SpotifyImageDto("https://playlist.image/large.jpg", 640, 640)),
            tracks = SpotifyPlaylistTracksDto(
                items = trackItems,
                total = 2,
                limit = 100,
                offset = 0,
                next = null,
                previous = null,
                href = null
            ),
            owner = null,
            public = true,
            collaborative = false,
            snapshotId = null,
            uri = null,
            externalUrls = null,
            href = null
        )
    }

    private fun createTrackDto(
        id: String,
        name: String,
        isLocal: Boolean
    ): SpotifyTrackDto {
        return SpotifyTrackDto(
            id = id,
            name = name,
            durationMs = 180000,
            popularity = 75,
            previewUrl = "https://preview.url/$id.mp3",
            artists = listOf(
                SpotifyArtistDto(
                    id = "artist1",
                    name = "Test Artist",
                    uri = "spotify:artist:artist1",
                    href = null,
                    externalUrls = null
                )
            ),
            album = SpotifyAlbumDto(
                id = "album1",
                name = "Test Album",
                images = listOf(SpotifyImageDto("https://album.image/large.jpg", 640, 640)),
                releaseDate = "2023-01-01",
                albumType = "album",
                uri = null,
                externalUrls = null
            ),
            uri = "spotify:track:$id",
            externalUrls = SpotifyExternalUrlsDto("https://open.spotify.com/track/$id"),
            explicit = false,
            href = null,
            isLocal = isLocal,
            trackNumber = 1
        )
    }
}

package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyExternalUrlsDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistOwnerDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyPlaylistTracksRefDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifySimplifiedPlaylistDto
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for SimplifiedPlaylistMapper
 * Tests the mapping from SpotifySimplifiedPlaylistDto to SimplifiedPlaylist domain model
 */
class SimplifiedPlaylistMapperTest {

    // ==================== toDomain Tests ====================

    @Test
    fun `toDomain should map id correctly`() {
        // Given
        val dto = createSimplifiedPlaylistDto(id = "playlist123")

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertEquals("playlist123", result.id)
    }

    @Test
    fun `toDomain should map name correctly`() {
        // Given
        val dto = createSimplifiedPlaylistDto(name = "My Awesome Playlist")

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertEquals("My Awesome Playlist", result.name)
    }

    @Test
    fun `toDomain should map description correctly`() {
        // Given
        val dto = createSimplifiedPlaylistDto(description = "A great playlist")

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertEquals("A great playlist", result.description)
    }

    @Test
    fun `toDomain should return null description when blank`() {
        // Given
        val dto = createSimplifiedPlaylistDto(description = "   ")

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertNull(result.description)
    }

    @Test
    fun `toDomain should return null description when null`() {
        // Given
        val dto = createSimplifiedPlaylistDto(description = null)

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertNull(result.description)
    }

    @Test
    fun `toDomain should map first image URL when images exist`() {
        // Given
        val images = listOf(
            SpotifyImageDto("https://large.jpg", 640, 640),
            SpotifyImageDto("https://medium.jpg", 300, 300)
        )
        val dto = createSimplifiedPlaylistDto(images = images)

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertEquals("https://large.jpg", result.imageUrl)
    }

    @Test
    fun `toDomain should return null imageUrl when images is empty`() {
        // Given
        val dto = createSimplifiedPlaylistDto(images = emptyList())

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertNull(result.imageUrl)
    }

    @Test
    fun `toDomain should map owner displayName correctly`() {
        // Given
        val owner = SpotifyPlaylistOwnerDto(
            id = "owner1",
            displayName = "Playlist Creator",
            externalUrls = null,
            href = null,
            uri = null
        )
        val dto = createSimplifiedPlaylistDto(owner = owner)

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertEquals("Playlist Creator", result.ownerName)
    }

    @Test
    fun `toDomain should return null ownerName when owner is null`() {
        // Given
        val dto = createSimplifiedPlaylistDto(owner = null)

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertNull(result.ownerName)
    }

    @Test
    fun `toDomain should map totalTracks from tracks ref`() {
        // Given
        val tracksRef = SpotifyPlaylistTracksRefDto(
            href = "https://api.spotify.com/v1/playlists/123/tracks",
            total = 75
        )
        val dto = createSimplifiedPlaylistDto(tracks = tracksRef)

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertEquals(75, result.totalTracks)
    }

    @Test
    fun `toDomain should return 0 totalTracks when tracks is null`() {
        // Given
        val dto = createSimplifiedPlaylistDto(tracks = null)

        // When
        val result = SimplifiedPlaylistMapper.toDomain(dto)

        // Then
        assertEquals(0, result.totalTracks)
    }

    // ==================== toDomainList Tests ====================

    @Test
    fun `toDomainList should map all playlists`() {
        // Given
        val dtos = listOf(
            createSimplifiedPlaylistDto(id = "playlist1"),
            createSimplifiedPlaylistDto(id = "playlist2"),
            createSimplifiedPlaylistDto(id = "playlist3")
        )

        // When
        val result = SimplifiedPlaylistMapper.toDomainList(dtos)

        // Then
        assertEquals(3, result.size)
        assertEquals("playlist1", result[0].id)
        assertEquals("playlist2", result[1].id)
        assertEquals("playlist3", result[2].id)
    }

    @Test
    fun `toDomainList should filter out null playlists`() {
        // Given
        val dtos: List<SpotifySimplifiedPlaylistDto?> = listOf(
            createSimplifiedPlaylistDto(id = "playlist1"),
            null,
            createSimplifiedPlaylistDto(id = "playlist3"),
            null
        )

        // When
        val result = SimplifiedPlaylistMapper.toDomainList(dtos)

        // Then
        assertEquals(2, result.size)
        assertEquals("playlist1", result[0].id)
        assertEquals("playlist3", result[1].id)
    }

    @Test
    fun `toDomainList should return empty list for empty input`() {
        // Given
        val dtos = emptyList<SpotifySimplifiedPlaylistDto?>()

        // When
        val result = SimplifiedPlaylistMapper.toDomainList(dtos)

        // Then
        assertEquals(0, result.size)
    }

    @Test
    fun `toDomainList should return empty list when all items are null`() {
        // Given
        val dtos: List<SpotifySimplifiedPlaylistDto?> = listOf(null, null, null)

        // When
        val result = SimplifiedPlaylistMapper.toDomainList(dtos)

        // Then
        assertEquals(0, result.size)
    }

    // ==================== Helper Functions ====================

    private fun createSimplifiedPlaylistDto(
        id: String = "playlist_id",
        name: String = "Playlist Name",
        description: String? = "Playlist description",
        images: List<SpotifyImageDto>? = listOf(SpotifyImageDto("https://default.jpg", 300, 300)),
        owner: SpotifyPlaylistOwnerDto? = SpotifyPlaylistOwnerDto(
            id = "owner1",
            displayName = "Default Owner",
            externalUrls = SpotifyExternalUrlsDto("https://open.spotify.com/user/owner1"),
            href = null,
            uri = null
        ),
        tracks: SpotifyPlaylistTracksRefDto? = SpotifyPlaylistTracksRefDto(
            href = "https://api.spotify.com/v1/playlists/$id/tracks",
            total = 50
        )
    ): SpotifySimplifiedPlaylistDto {
        return SpotifySimplifiedPlaylistDto(
            id = id,
            name = name,
            description = description,
            images = images,
            owner = owner,
            public = true,
            collaborative = false,
            snapshotId = "snapshot123",
            uri = "spotify:playlist:$id",
            externalUrls = SpotifyExternalUrlsDto("https://open.spotify.com/playlist/$id"),
            href = "https://api.spotify.com/v1/playlists/$id",
            tracks = tracks
        )
    }
}

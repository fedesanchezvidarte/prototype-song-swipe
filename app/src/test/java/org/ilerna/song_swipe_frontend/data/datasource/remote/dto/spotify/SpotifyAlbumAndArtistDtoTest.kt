package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.Gson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for Spotify Album and Artist DTOs
 * Tests JSON deserialization from real Spotify API response structure
 */
class SpotifyAlbumAndArtistDtoTest {

    private val gson = Gson()

    // ==================== Album Deserialization Tests ====================

    @Test
    fun `SpotifyAlbumDto deserializes correctly from playlist track response`() {
        // Given - Simplified album object from track in playlist
        val json = """
        {
          "id": "6T7BxdF1tKzCq9hDpwKDqh",
          "name": "Cut To The Feeling",
          "images": [
            {
              "url": "https://i.scdn.co/image/ab67616d0000b273e3e3b64cea45265469d4cafa",
              "height": 640,
              "width": 640
            },
            {
              "url": "https://i.scdn.co/image/ab67616d00001e02e3e3b64cea45265469d4cafa",
              "height": 300,
              "width": 300
            },
            {
              "url": "https://i.scdn.co/image/ab67616d00004851e3e3b64cea45265469d4cafa",
              "height": 64,
              "width": 64
            }
          ],
          "release_date": "2017-05-26",
          "album_type": "single",
          "uri": "spotify:album:6T7BxdF1tKzCq9hDpwKDqh",
          "external_urls": {
            "spotify": "https://open.spotify.com/album/6T7BxdF1tKzCq9hDpwKDqh"
          }
        }
        """

        // When
        val album = gson.fromJson(json, SpotifyAlbumDto::class.java)

        // Then
        assertEquals("6T7BxdF1tKzCq9hDpwKDqh", album.id)
        assertEquals("Cut To The Feeling", album.name)
        assertEquals("2017-05-26", album.releaseDate)
        assertEquals("single", album.albumType)
        assertEquals("spotify:album:6T7BxdF1tKzCq9hDpwKDqh", album.uri)

        // Verify images
        assertNotNull(album.images)
        assertEquals(3, album.images.size)
        assertEquals(640, album.images[0].height)
        assertEquals(300, album.images[1].height)
        assertEquals(64, album.images[2].height)

        // Verify external URLs
        assertNotNull(album.externalUrls)
        assertEquals(
            "https://open.spotify.com/album/6T7BxdF1tKzCq9hDpwKDqh",
            album.externalUrls.spotify
        )
    }

    @Test
    fun `SpotifyAlbumDto handles different album types`() {
        // Given - Album type: album, single, compilation
        val albumJson = """
        {
          "id": "album1",
          "name": "Full Album",
          "images": [],
          "release_date": "2020-01-01",
          "album_type": "album"
        }
        """

        val singleJson = """
        {
          "id": "single1",
          "name": "Single",
          "images": [],
          "release_date": "2021-06-15",
          "album_type": "single"
        }
        """

        val compilationJson = """
        {
          "id": "compilation1",
          "name": "Greatest Hits",
          "images": [],
          "release_date": "2022-12-01",
          "album_type": "compilation"
        }
        """

        // When
        val album = gson.fromJson(albumJson, SpotifyAlbumDto::class.java)
        val single = gson.fromJson(singleJson, SpotifyAlbumDto::class.java)
        val compilation = gson.fromJson(compilationJson, SpotifyAlbumDto::class.java)

        // Then
        assertEquals("album", album.albumType)
        assertEquals("single", single.albumType)
        assertEquals("compilation", compilation.albumType)
    }

    @Test
    fun `SpotifyAlbumDto handles year-only release date`() {
        // Given - Some albums only have year precision
        val json = """
        {
          "id": "oldalbum",
          "name": "Classic Album",
          "images": [],
          "release_date": "1981",
          "album_type": "album"
        }
        """

        // When
        val album = gson.fromJson(json, SpotifyAlbumDto::class.java)

        // Then
        assertEquals("1981", album.releaseDate)
    }

    // ==================== Artist Deserialization Tests ====================

    @Test
    fun `SpotifyArtistDto deserializes correctly from playlist track response`() {
        // Given - Simplified artist object from track in playlist
        val json = """
        {
          "id": "6sFIWsNpZYqfjUpaCgueju",
          "name": "Carly Rae Jepsen",
          "uri": "spotify:artist:6sFIWsNpZYqfjUpaCgueju",
          "href": "https://api.spotify.com/v1/artists/6sFIWsNpZYqfjUpaCgueju",
          "external_urls": {
            "spotify": "https://open.spotify.com/artist/6sFIWsNpZYqfjUpaCgueju"
          }
        }
        """

        // When
        val artist = gson.fromJson(json, SpotifyArtistDto::class.java)

        // Then
        assertEquals("6sFIWsNpZYqfjUpaCgueju", artist.id)
        assertEquals("Carly Rae Jepsen", artist.name)
        assertEquals("spotify:artist:6sFIWsNpZYqfjUpaCgueju", artist.uri)
        assertEquals("https://api.spotify.com/v1/artists/6sFIWsNpZYqfjUpaCgueju", artist.href)
        assertNotNull(artist.externalUrls)
        assertEquals(
            "https://open.spotify.com/artist/6sFIWsNpZYqfjUpaCgueju",
            artist.externalUrls?.spotify
        )
    }

    @Test
    fun `SpotifyArtistDto handles minimal required fields`() {
        // Given - Only required fields
        val json = """
        {
          "id": "artist789",
          "name": "Minimal Artist",
          "uri": "spotify:artist:artist789"
        }
        """

        // When
        val artist = gson.fromJson(json, SpotifyArtistDto::class.java)

        // Then
        assertEquals("artist789", artist.id)
        assertEquals("Minimal Artist", artist.name)
        assertEquals("spotify:artist:artist789", artist.uri)
        assertNull(artist.href)
        assertNull(artist.externalUrls)
    }

    // ==================== External URLs Deserialization Tests ====================

    @Test
    fun `SpotifyExternalUrlsDto deserializes correctly`() {
        // Given
        val json = """
        {
          "spotify": "https://open.spotify.com/track/11dFghVXANMlKmJXsNCbNl"
        }
        """

        // When
        val externalUrls = gson.fromJson(json, SpotifyExternalUrlsDto::class.java)

        // Then
        assertEquals("https://open.spotify.com/track/11dFghVXANMlKmJXsNCbNl", externalUrls.spotify)
    }

    @Test
    fun `SpotifyExternalUrlsDto is reusable across different object types`() {
        // Given - External URLs from track, album, and artist
        val trackUrl = """{"spotify": "https://open.spotify.com/track/xyz"}"""
        val albumUrl = """{"spotify": "https://open.spotify.com/album/abc"}"""
        val artistUrl = """{"spotify": "https://open.spotify.com/artist/def"}"""

        // When
        val trackExtUrls = gson.fromJson(trackUrl, SpotifyExternalUrlsDto::class.java)
        val albumExtUrls = gson.fromJson(albumUrl, SpotifyExternalUrlsDto::class.java)
        val artistExtUrls = gson.fromJson(artistUrl, SpotifyExternalUrlsDto::class.java)

        // Then - All deserialize correctly with same DTO
        assertNotNull(trackExtUrls.spotify)
        assertNotNull(albumExtUrls.spotify)
        assertNotNull(artistExtUrls.spotify)
    }
}

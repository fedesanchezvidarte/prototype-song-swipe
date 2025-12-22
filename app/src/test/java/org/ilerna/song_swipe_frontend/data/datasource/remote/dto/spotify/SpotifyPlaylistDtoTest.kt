package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.Gson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for Spotify Playlist DTOs
 * Tests JSON deserialization from real Spotify API response structure
 */
class SpotifyPlaylistDtoTest {

    private val gson = Gson()

    // ==================== Playlist Deserialization Tests ====================

    @Test
    fun `SpotifyPlaylistDto deserializes correctly from complete API response`() {
        // Given - Real Spotify API response structure
        val json = """
        {
          "id": "37i9dQZF1DWXRqgorJj26U",
          "name": "Rock Classics",
          "description": "Rock legends & epic songs. Cover: Foo Fighters",
          "images": [
            {
              "url": "https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228",
              "height": 300,
              "width": 300
            }
          ],
          "tracks": {
            "items": [],
            "total": 50,
            "limit": 20,
            "offset": 0,
            "next": "https://api.spotify.com/v1/playlists/37i9dQZF1DWXRqgorJj26U/tracks?offset=20",
            "previous": null,
            "href": "https://api.spotify.com/v1/playlists/37i9dQZF1DWXRqgorJj26U/tracks"
          },
          "owner": {
            "id": "spotify",
            "display_name": "Spotify",
            "uri": "spotify:user:spotify",
            "href": "https://api.spotify.com/v1/users/spotify",
            "external_urls": {
              "spotify": "https://open.spotify.com/user/spotify"
            }
          },
          "public": true,
          "collaborative": false,
          "snapshot_id": "MTYzNjM2MzYwMCwwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMA==",
          "uri": "spotify:playlist:37i9dQZF1DWXRqgorJj26U",
          "external_urls": {
            "spotify": "https://open.spotify.com/playlist/37i9dQZF1DWXRqgorJj26U"
          },
          "href": "https://api.spotify.com/v1/playlists/37i9dQZF1DWXRqgorJj26U"
        }
        """

        // When
        val playlist = gson.fromJson(json, SpotifyPlaylistDto::class.java)

        // Then
        assertEquals("37i9dQZF1DWXRqgorJj26U", playlist.id)
        assertEquals("Rock Classics", playlist.name)
        assertEquals("Rock legends & epic songs. Cover: Foo Fighters", playlist.description)
        assertNotNull(playlist.images)
        assertEquals(1, playlist.images?.size)
        assertEquals("https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228", playlist.images?.firstOrNull()?.url)
        assertNotNull(playlist.tracks)
        assertEquals(50, playlist.tracks.total)
        assertEquals(true, playlist.public)
        assertEquals(false, playlist.collaborative)
        assertEquals("spotify:playlist:37i9dQZF1DWXRqgorJj26U", playlist.uri)
    }

    @Test
    fun `SpotifyPlaylistDto handles null description`() {
        // Given - Playlist without description (user playlists may not have one)
        val json = """
        {
          "id": "user123playlist",
          "name": "My Playlist",
          "description": null,
          "images": [],
          "tracks": {
            "items": [],
            "total": 0,
            "limit": 20,
            "offset": 0,
            "next": null,
            "previous": null,
            "href": "https://api.spotify.com/v1/playlists/user123playlist/tracks"
          },
          "public": false
        }
        """

        // When
        val playlist = gson.fromJson(json, SpotifyPlaylistDto::class.java)

        // Then
        assertEquals("user123playlist", playlist.id)
        assertEquals("My Playlist", playlist.name)
        assertNull(playlist.description)
        assertEquals(0, playlist.tracks.total)
        assertEquals(false, playlist.public)
    }

    // ==================== Playlist Tracks Paging Deserialization Tests ====================

    @Test
    fun `SpotifyPlaylistTracksDto deserializes with pagination info`() {
        // Given
        val json = """
        {
          "items": [],
          "total": 150,
          "limit": 20,
          "offset": 0,
          "next": "https://api.spotify.com/v1/playlists/xyz/tracks?offset=20",
          "previous": null,
          "href": "https://api.spotify.com/v1/playlists/xyz/tracks"
        }
        """

        // When
        val tracks = gson.fromJson(json, SpotifyPlaylistTracksDto::class.java)

        // Then
        assertEquals(150, tracks.total)
        assertEquals(20, tracks.limit)
        assertEquals(0, tracks.offset)
        assertNotNull(tracks.next)
        assertNull(tracks.previous)
        assertEquals(0, tracks.items.size)
    }

    @Test
    fun `SpotifyPlaylistTracksDto handles last page with no next`() {
        // Given - Last page of results
        val json = """
        {
          "items": [],
          "total": 150,
          "limit": 20,
          "offset": 140,
          "next": null,
          "previous": "https://api.spotify.com/v1/playlists/xyz/tracks?offset=120",
          "href": "https://api.spotify.com/v1/playlists/xyz/tracks"
        }
        """

        // When
        val tracks = gson.fromJson(json, SpotifyPlaylistTracksDto::class.java)

        // Then
        assertEquals(150, tracks.total)
        assertEquals(140, tracks.offset)
        assertNull(tracks.next)
        assertNotNull(tracks.previous)
    }

    // ==================== Playlist Owner Deserialization Tests ====================

    @Test
    fun `SpotifyPlaylistOwnerDto deserializes correctly`() {
        // Given
        val json = """
        {
          "id": "spotify",
          "display_name": "Spotify",
          "uri": "spotify:user:spotify",
          "href": "https://api.spotify.com/v1/users/spotify",
          "external_urls": {
            "spotify": "https://open.spotify.com/user/spotify"
          }
        }
        """

        // When
        val owner = gson.fromJson(json, SpotifyPlaylistOwnerDto::class.java)

        // Then
        assertEquals("spotify", owner.id)
        assertEquals("Spotify", owner.displayName)
        assertEquals("spotify:user:spotify", owner.uri)
        assertNotNull(owner.externalUrls)
        assertEquals("https://open.spotify.com/user/spotify", owner.externalUrls?.spotify)
    }

    @Test
    fun `SpotifyPlaylistOwnerDto handles null display name`() {
        // Given - Some users may not have display name
        val json = """
        {
          "id": "user123",
          "display_name": null,
          "uri": "spotify:user:user123"
        }
        """

        // When
        val owner = gson.fromJson(json, SpotifyPlaylistOwnerDto::class.java)

        // Then
        assertEquals("user123", owner.id)
        assertNull(owner.displayName)
    }
}

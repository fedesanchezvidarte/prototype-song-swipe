package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.Gson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for Spotify Track DTOs
 * Tests JSON deserialization from real Spotify API response structure
 */
class SpotifyTrackDtoTest {

    private val gson = Gson()

    // ==================== Track Deserialization Tests ====================

    @Test
    fun `SpotifyTrackDto deserializes correctly from complete API response`() {
        // Given - Real Spotify track object from playlist response
        val json = """
        {
          "id": "11dFghVXANMlKmJXsNCbNl",
          "name": "Cut To The Feeling",
          "duration_ms": 207959,
          "popularity": 63,
          "preview_url": "https://p.scdn.co/mp3-preview/3eb16018c2a700240e9dfb8817b6f2d041f15eb1",
          "artists": [
            {
              "id": "6sFIWsNpZYqfjUpaCgueju",
              "name": "Carly Rae Jepsen",
              "uri": "spotify:artist:6sFIWsNpZYqfjUpaCgueju",
              "href": "https://api.spotify.com/v1/artists/6sFIWsNpZYqfjUpaCgueju",
              "external_urls": {
                "spotify": "https://open.spotify.com/artist/6sFIWsNpZYqfjUpaCgueju"
              }
            }
          ],
          "album": {
            "id": "6T7BxdF1tKzCq9hDpwKDqh",
            "name": "Cut To The Feeling",
            "images": [
              {
                "url": "https://i.scdn.co/image/ab67616d0000b273e3e3b64cea45265469d4cafa",
                "height": 640,
                "width": 640
              }
            ],
            "release_date": "2017-05-26",
            "album_type": "single",
            "uri": "spotify:album:6T7BxdF1tKzCq9hDpwKDqh",
            "external_urls": {
              "spotify": "https://open.spotify.com/album/6T7BxdF1tKzCq9hDpwKDqh"
            }
          },
          "uri": "spotify:track:11dFghVXANMlKmJXsNCbNl",
          "external_urls": {
            "spotify": "https://open.spotify.com/track/11dFghVXANMlKmJXsNCbNl"
          },
          "explicit": false,
          "href": "https://api.spotify.com/v1/tracks/11dFghVXANMlKmJXsNCbNl",
          "is_local": false,
          "track_number": 1
        }
        """

        // When
        val track = gson.fromJson(json, SpotifyTrackDto::class.java)

        // Then
        assertEquals("11dFghVXANMlKmJXsNCbNl", track.id)
        assertEquals("Cut To The Feeling", track.name)
        assertEquals(207959, track.durationMs)
        assertEquals(63, track.popularity)
        assertEquals(
            "https://p.scdn.co/mp3-preview/3eb16018c2a700240e9dfb8817b6f2d041f15eb1",
            track.previewUrl
        )
        assertEquals("spotify:track:11dFghVXANMlKmJXsNCbNl", track.uri)
        assertEquals(false, track.explicit)
        assertEquals(false, track.isLocal)
        assertEquals(1, track.trackNumber)

        // Verify artists
        assertNotNull(track.artists)
        assertEquals(1, track.artists.size)
        assertEquals("Carly Rae Jepsen", track.artists.first().name)

        // Verify album
        assertNotNull(track.album)
        assertEquals("Cut To The Feeling", track.album.name)
        assertEquals("2017-05-26", track.album.releaseDate)

        // Verify external URLs
        assertEquals(
            "https://open.spotify.com/track/11dFghVXANMlKmJXsNCbNl",
            track.externalUrls.spotify
        )
    }

    @Test
    fun `SpotifyTrackDto handles null preview URL`() {
        // Given - Some tracks don't have preview URLs
        val json = """
        {
          "id": "track123",
          "name": "Song Without Preview",
          "duration_ms": 180000,
          "popularity": 50,
          "preview_url": null,
          "artists": [
            {
              "id": "artist123",
              "name": "Artist Name",
              "uri": "spotify:artist:artist123"
            }
          ],
          "album": {
            "id": "album123",
            "name": "Album Name",
            "images": [],
            "release_date": "2020-01-01"
          },
          "uri": "spotify:track:track123",
          "external_urls": {
            "spotify": "https://open.spotify.com/track/track123"
          }
        }
        """

        // When
        val track = gson.fromJson(json, SpotifyTrackDto::class.java)

        // Then
        assertEquals("track123", track.id)
        assertEquals("Song Without Preview", track.name)
        assertNull(track.previewUrl)
        assertNotNull(track.artists)
        assertNotNull(track.album)
    }

    @Test
    fun `SpotifyTrackDto handles multiple artists`() {
        // Given - Track with multiple artists (collaboration)
        val json = """
        {
          "id": "collab123",
          "name": "Collaboration Song",
          "duration_ms": 200000,
          "popularity": 80,
          "preview_url": null,
          "artists": [
            {
              "id": "artist1",
              "name": "Artist One",
              "uri": "spotify:artist:artist1"
            },
            {
              "id": "artist2",
              "name": "Artist Two",
              "uri": "spotify:artist:artist2"
            },
            {
              "id": "artist3",
              "name": "Artist Three",
              "uri": "spotify:artist:artist3"
            }
          ],
          "album": {
            "id": "album123",
            "name": "Album",
            "images": [],
            "release_date": "2023-01-01"
          },
          "uri": "spotify:track:collab123",
          "external_urls": {
            "spotify": "https://open.spotify.com/track/collab123"
          }
        }
        """

        // When
        val track = gson.fromJson(json, SpotifyTrackDto::class.java)

        // Then
        assertEquals(3, track.artists.size)
        assertEquals("Artist One", track.artists[0].name)
        assertEquals("Artist Two", track.artists[1].name)
        assertEquals("Artist Three", track.artists[2].name)
    }

    // ==================== Playlist Track Item Deserialization Tests ====================

    @Test
    fun `SpotifyPlaylistTrackDto deserializes with metadata`() {
        // Given - Track item from playlist with added_at and added_by
        val json = """
        {
          "added_at": "2023-01-15T10:30:00Z",
          "added_by": {
            "id": "user123",
            "uri": "spotify:user:user123",
            "href": "https://api.spotify.com/v1/users/user123"
          },
          "is_local": false,
          "track": {
            "id": "track456",
            "name": "Track Name",
            "duration_ms": 180000,
            "popularity": 70,
            "preview_url": "https://preview.url",
            "artists": [
              {
                "id": "artist456",
                "name": "Artist",
                "uri": "spotify:artist:artist456"
              }
            ],
            "album": {
              "id": "album456",
              "name": "Album",
              "images": [],
              "release_date": "2022-01-01"
            },
            "uri": "spotify:track:track456",
            "external_urls": {
              "spotify": "https://open.spotify.com/track/track456"
            }
          }
        }
        """

        // When
        val playlistTrack = gson.fromJson(json, SpotifyPlaylistTrackDto::class.java)

        // Then
        assertEquals("2023-01-15T10:30:00Z", playlistTrack.addedAt)
        assertNotNull(playlistTrack.addedBy)
        assertEquals("user123", playlistTrack.addedBy.id)
        assertEquals(false, playlistTrack.isLocal)
        assertNotNull(playlistTrack.track)
        assertEquals("Track Name", playlistTrack.track.name)
    }

    @Test
    fun `SpotifyPlaylistTrackDto handles null track gracefully`() {
        // Given - Sometimes tracks can be null (removed/unavailable)
        val json = """
        {
          "added_at": "2023-01-15T10:30:00Z",
          "added_by": null,
          "is_local": false,
          "track": null
        }
        """

        // When
        val playlistTrack = gson.fromJson(json, SpotifyPlaylistTrackDto::class.java)

        // Then
        assertNull(playlistTrack.track)
        assertNull(playlistTrack.addedBy)
        assertEquals("2023-01-15T10:30:00Z", playlistTrack.addedAt)
    }
}

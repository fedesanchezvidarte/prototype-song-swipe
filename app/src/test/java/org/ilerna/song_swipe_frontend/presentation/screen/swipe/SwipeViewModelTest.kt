package org.ilerna.song_swipe_frontend.presentation.screen.swipe

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.Album
import org.ilerna.song_swipe_frontend.domain.model.Artist
import org.ilerna.song_swipe_frontend.domain.model.Track
import org.ilerna.song_swipe_frontend.domain.usecase.playlist.GetPlaylistTracksUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for SwipeViewModel
 * Tests track loading and swipe actions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SwipeViewModelTest {

    private lateinit var viewModel: SwipeViewModel
    private lateinit var mockGetPlaylistTracksUseCase: GetPlaylistTracksUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockGetPlaylistTracksUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTracks sets Success state when use case succeeds`() = runTest {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"))
        coEvery { mockGetPlaylistTracksUseCase() } returns NetworkResult.Success(tracks)
        viewModel = SwipeViewModel(mockGetPlaylistTracksUseCase)

        // When
        viewModel.loadTracks()

        // Then
        val state = viewModel.state.value
        assertTrue(state is SwipeState.Success)
        assertEquals(2, (state as SwipeState.Success).tracks.size)
        assertEquals(0, state.currentIndex)
    }

    @Test
    fun `loadTracks sets Error state when use case fails`() = runTest {
        // Given
        coEvery { mockGetPlaylistTracksUseCase() } returns NetworkResult.Error("API error", 401)
        viewModel = SwipeViewModel(mockGetPlaylistTracksUseCase)

        // When
        viewModel.loadTracks()

        // Then
        val state = viewModel.state.value
        assertTrue(state is SwipeState.Error)
        assertEquals("API error", (state as SwipeState.Error).message)
    }

    @Test
    fun `loadTracks sets Error state when tracks list is empty`() = runTest {
        // Given
        coEvery { mockGetPlaylistTracksUseCase() } returns NetworkResult.Success(emptyList())
        viewModel = SwipeViewModel(mockGetPlaylistTracksUseCase)

        // When
        viewModel.loadTracks()

        // Then
        val state = viewModel.state.value
        assertTrue(state is SwipeState.Error)
        assertEquals("No tracks found in playlist", (state as SwipeState.Error).message)
    }

    @Test
    fun `onLike increments index and adds to likedTracks`() = runTest {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"))
        coEvery { mockGetPlaylistTracksUseCase() } returns NetworkResult.Success(tracks)
        viewModel = SwipeViewModel(mockGetPlaylistTracksUseCase)
        viewModel.loadTracks()

        // When
        viewModel.onLike()

        // Then
        val state = viewModel.state.value as SwipeState.Success
        assertEquals(1, state.currentIndex)
        assertEquals(listOf("1"), state.likedTracks)
        assertEquals(emptyList<String>(), state.dislikedTracks)
    }

    @Test
    fun `onDislike increments index and adds to dislikedTracks`() = runTest {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"))
        coEvery { mockGetPlaylistTracksUseCase() } returns NetworkResult.Success(tracks)
        viewModel = SwipeViewModel(mockGetPlaylistTracksUseCase)
        viewModel.loadTracks()

        // When
        viewModel.onDislike()

        // Then
        val state = viewModel.state.value as SwipeState.Success
        assertEquals(1, state.currentIndex)
        assertEquals(emptyList<String>(), state.likedTracks)
        assertEquals(listOf("1"), state.dislikedTracks)
    }

    @Test
    fun `onSkip increments index without recording preference`() = runTest {
        // Given
        val tracks = listOf(createTrack("1"), createTrack("2"))
        coEvery { mockGetPlaylistTracksUseCase() } returns NetworkResult.Success(tracks)
        viewModel = SwipeViewModel(mockGetPlaylistTracksUseCase)
        viewModel.loadTracks()

        // When
        viewModel.onSkip()

        // Then
        val state = viewModel.state.value as SwipeState.Success
        assertEquals(1, state.currentIndex)
        assertEquals(emptyList<String>(), state.likedTracks)
        assertEquals(emptyList<String>(), state.dislikedTracks)
    }

    // ==================== Helper Functions ====================

    private fun createTrack(id: String): Track {
        return Track(
            id = id,
            name = "Track $id",
            artists = listOf(Artist("artist1", "Artist", "spotify:artist:1")),
            album = Album("album1", "Album", null, "2023"),
            durationMs = 180000,
            popularity = 75,
            previewUrl = null,
            spotifyUri = "spotify:track:$id",
            externalUrl = "https://open.spotify.com/track/$id"
        )
    }
}

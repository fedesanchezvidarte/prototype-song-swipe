package org.ilerna.song_swipe_frontend.presentation.screen.home

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.ilerna.song_swipe_frontend.core.state.UiState
import org.ilerna.song_swipe_frontend.domain.model.MusicCategory
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for HomeViewModel
 * Tests category loading, error handling, and refresh functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val categoryRepository: CategoryRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockCategories = listOf(
        MusicCategory("1", "Pop"),
        MusicCategory("2", "Rock"),
        MusicCategory("3", "Electronic")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Success State Tests ====================

    @Test
    fun `init should load categories and emit Success state`() = runTest {
        coEvery { categoryRepository.getCategories() } returns Result.success(mockCategories)

        viewModel = HomeViewModel(categoryRepository)

        val state = viewModel.categoriesState.value
        assertTrue(state is UiState.Success)
        assertEquals(3, state.data.size)
        assertEquals("Pop", state.data[0].name)
    }

    @Test
    fun `loadCategories should handle empty list`() = runTest {
        coEvery { categoryRepository.getCategories() } returns Result.success(emptyList())

        viewModel = HomeViewModel(categoryRepository)

        val state = viewModel.categoriesState.value
        assertTrue(state is UiState.Success)
        assertTrue(state.data.isEmpty())
    }

    // ==================== Error State Tests ====================

    @Test
    fun `loadCategories should emit Error state when repository fails`() = runTest {
        val errorMessage = "Network error"
        coEvery { categoryRepository.getCategories() } returns Result.failure(
            RuntimeException(
                errorMessage
            )
        )

        viewModel = HomeViewModel(categoryRepository)

        val state = viewModel.categoriesState.value
        assertTrue(state is UiState.Error)
        assertEquals(errorMessage, state.message)
    }

    // ==================== Refresh Tests ====================

    @Test
    fun `refresh should reload categories`() = runTest {
        val initialCategories = listOf(MusicCategory("1", "Pop"))
        val updatedCategories = listOf(
            MusicCategory("1", "Pop"),
            MusicCategory("2", "Rock")
        )

        coEvery { categoryRepository.getCategories() } returns Result.success(initialCategories)
        viewModel = HomeViewModel(categoryRepository)
        assertEquals(1, (viewModel.categoriesState.value as UiState.Success).data.size)

        coEvery { categoryRepository.getCategories() } returns Result.success(updatedCategories)
        viewModel.refresh()

        val state = viewModel.categoriesState.value
        assertTrue(state is UiState.Success)
        assertEquals(2, state.data.size)
    }

    @Test
    fun `refresh after error should retry successfully`() = runTest {
        coEvery { categoryRepository.getCategories() } returns Result.failure(RuntimeException("Network error"))
        viewModel = HomeViewModel(categoryRepository)
        assertTrue(viewModel.categoriesState.value is UiState.Error)

        coEvery { categoryRepository.getCategories() } returns Result.success(mockCategories)
        viewModel.refresh()

        val state = viewModel.categoriesState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockCategories, state.data)
    }
}

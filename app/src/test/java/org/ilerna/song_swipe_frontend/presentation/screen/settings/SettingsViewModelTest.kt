package org.ilerna.song_swipe_frontend.presentation.screen.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ThemeMode
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for SettingsViewModel
 * Tests theme mode state management and DataStore interaction
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial theme is SYSTEM`() = runTest(testDispatcher) {
        // Given
        val fakeSettingsDataStore = FakeSettingsDataStore()
        val viewModel = SettingsViewModel(fakeSettingsDataStore)

        // Then - initial value should be SYSTEM
        assertEquals(ThemeMode.SYSTEM, viewModel.currentTheme.value)
    }

    @Test
    fun `setTheme persists DARK to DataStore`() = runTest(testDispatcher) {
        // Given
        val fakeSettingsDataStore = FakeSettingsDataStore()
        val viewModel = SettingsViewModel(fakeSettingsDataStore)

        // When
        viewModel.setTheme(ThemeMode.DARK)

        // Then - verify DataStore received the value
        assertEquals(ThemeMode.DARK, fakeSettingsDataStore.lastSavedTheme)
        assertEquals(ThemeMode.DARK, fakeSettingsDataStore.themeMode.first())
    }

    @Test
    fun `setTheme persists LIGHT to DataStore`() = runTest(testDispatcher) {
        // Given
        val fakeSettingsDataStore = FakeSettingsDataStore()
        val viewModel = SettingsViewModel(fakeSettingsDataStore)

        // When
        viewModel.setTheme(ThemeMode.LIGHT)

        // Then - verify DataStore received the value
        assertEquals(ThemeMode.LIGHT, fakeSettingsDataStore.lastSavedTheme)
        assertEquals(ThemeMode.LIGHT, fakeSettingsDataStore.themeMode.first())
    }

    @Test
    fun `setTheme persists SYSTEM to DataStore`() = runTest(testDispatcher) {
        // Given
        val fakeSettingsDataStore = FakeSettingsDataStore(initialTheme = ThemeMode.DARK)
        val viewModel = SettingsViewModel(fakeSettingsDataStore)

        // When
        viewModel.setTheme(ThemeMode.SYSTEM)

        // Then - verify DataStore received the value
        assertEquals(ThemeMode.SYSTEM, fakeSettingsDataStore.lastSavedTheme)
        assertEquals(ThemeMode.SYSTEM, fakeSettingsDataStore.themeMode.first())
    }

    @Test
    fun `multiple setTheme calls persist correctly to DataStore`() = runTest(testDispatcher) {
        // Given
        val fakeSettingsDataStore = FakeSettingsDataStore()
        val viewModel = SettingsViewModel(fakeSettingsDataStore)

        // When/Then - change through all themes
        viewModel.setTheme(ThemeMode.LIGHT)
        assertEquals(ThemeMode.LIGHT, fakeSettingsDataStore.lastSavedTheme)

        viewModel.setTheme(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, fakeSettingsDataStore.lastSavedTheme)

        viewModel.setTheme(ThemeMode.SYSTEM)
        assertEquals(ThemeMode.SYSTEM, fakeSettingsDataStore.lastSavedTheme)
    }
}

/**
 * Fake implementation of ISettingsDataStore for testing
 */
class FakeSettingsDataStore(initialTheme: ThemeMode = ThemeMode.SYSTEM) : ISettingsDataStore {
    private val _themeMode = MutableStateFlow(initialTheme)
    override val themeMode: Flow<ThemeMode> = _themeMode

    var lastSavedTheme: ThemeMode? = null
        private set

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        lastSavedTheme = themeMode
        _themeMode.value = themeMode
    }
}

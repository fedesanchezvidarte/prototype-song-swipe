package org.ilerna.song_swipe_frontend.data.datasource.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.UUID
import kotlin.test.assertEquals

/**
 * Unit tests for SettingsDataStore
 * Tests theme mode persistence, retrieval, error handling, and Flow emissions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsDataStoreTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private fun createTestDataStore(testScope: TestScope): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File(tmpFolder.root, "test_settings_${UUID.randomUUID()}.preferences_pb") }
        )
    }

    @Test
    fun `themeMode returns SYSTEM as default when no preference is set`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val settingsDataStore = TestableSettingsDataStore(testDataStore)

        // When
        val themeMode = settingsDataStore.themeMode.first()

        // Then
        assertEquals(ThemeMode.SYSTEM, themeMode)
    }

    @Test
    fun `setThemeMode persists DARK theme correctly`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val settingsDataStore = TestableSettingsDataStore(testDataStore)

        // When
        settingsDataStore.setThemeMode(ThemeMode.DARK)

        // Then
        val savedTheme = settingsDataStore.themeMode.first()
        assertEquals(ThemeMode.DARK, savedTheme)
    }

    @Test
    fun `setThemeMode persists LIGHT theme correctly`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val settingsDataStore = TestableSettingsDataStore(testDataStore)

        // When
        settingsDataStore.setThemeMode(ThemeMode.LIGHT)

        // Then
        val savedTheme = settingsDataStore.themeMode.first()
        assertEquals(ThemeMode.LIGHT, savedTheme)
    }

    @Test
    fun `themeMode returns SYSTEM when invalid value is stored`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val settingsDataStore = TestableSettingsDataStore(testDataStore)

        // Given - store an invalid theme value directly
        testDataStore.edit { preferences ->
            preferences[stringPreferencesKey("theme_mode")] = "INVALID_THEME"
        }

        // When
        val themeMode = settingsDataStore.themeMode.first()

        // Then - should fallback to SYSTEM
        assertEquals(ThemeMode.SYSTEM, themeMode)
    }

    @Test
    fun `themeMode emits new value when theme is changed`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val settingsDataStore = TestableSettingsDataStore(testDataStore)

        val initialTheme = settingsDataStore.themeMode.first()
        assertEquals(ThemeMode.SYSTEM, initialTheme)

        // When
        settingsDataStore.setThemeMode(ThemeMode.DARK)

        // Then
        val newTheme = settingsDataStore.themeMode.first()
        assertEquals(ThemeMode.DARK, newTheme)
    }
}

/**
 * Testable version of SettingsDataStore that accepts a DataStore directly
 * This allows us to use a test DataStore instead of one tied to Context
 */
class TestableSettingsDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    val themeMode = dataStore.data.map { preferences ->
        val themeName = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(themeName)
        } catch (_: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }
}

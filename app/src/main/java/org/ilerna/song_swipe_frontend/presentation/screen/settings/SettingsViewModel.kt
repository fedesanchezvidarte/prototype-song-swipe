package org.ilerna.song_swipe_frontend.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.SettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ThemeMode

/**
 * SettingsViewModel - Manages settings state including theme preference
 * Persists settings using DataStore Preferences
 */
class SettingsViewModel(
    private val settingsDataStore: ISettingsDataStore
) : ViewModel() {
    
    /**
     * Current theme mode as a StateFlow
     */
    val currentTheme: StateFlow<ThemeMode> = settingsDataStore.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )
    
    /**
     * Set theme mode and persist to DataStore
     */
    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            settingsDataStore.setThemeMode(theme)
        }
    }
}

/**
 * Factory for creating SettingsViewModel with SettingsDataStore dependency
 */
class SettingsViewModelFactory(
    private val settingsDataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

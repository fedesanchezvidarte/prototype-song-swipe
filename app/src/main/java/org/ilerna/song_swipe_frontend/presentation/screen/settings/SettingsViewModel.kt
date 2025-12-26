package org.ilerna.song_swipe_frontend.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ThemeMode
import org.ilerna.song_swipe_frontend.domain.usecase.LoginUseCase
import javax.inject.Inject

/**
 * SettingsViewModel - Manages settings state and user actions.
 * Persists settings using DataStore Preferences
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: ISettingsDataStore,
    private val loginUseCase: LoginUseCase
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
    
    private val _isSigningOut = MutableStateFlow(false)
    val isSigningOut: StateFlow<Boolean> = _isSigningOut.asStateFlow()
    
    // Event to notify that sign out completed successfully
    private val _signOutComplete = MutableSharedFlow<Unit>()
    val signOutComplete: SharedFlow<Unit> = _signOutComplete.asSharedFlow()
    
    /**
     * Set theme mode and persist to DataStore
     */
    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            settingsDataStore.setThemeMode(theme)
        }
    }
    
    /**
     * Sign out the current user
     * Emits signOutComplete event when done to trigger navigation
     */
    fun signOut() {
        viewModelScope.launch {
            _isSigningOut.value = true
            try {
                loginUseCase.signOut()
                _signOutComplete.emit(Unit)
            } catch (e: Exception) {
                // Log error but still set signing out to false
                // The UI will remain in the signed-in state on error
            } finally {
                _isSigningOut.value = false
            }
        }
    }
}

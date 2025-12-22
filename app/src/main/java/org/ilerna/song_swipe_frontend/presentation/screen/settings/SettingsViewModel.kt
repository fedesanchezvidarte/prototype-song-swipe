package org.ilerna.song_swipe_frontend.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.SettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ThemeMode
import org.ilerna.song_swipe_frontend.domain.usecase.LoginUseCase
import org.ilerna.song_swipe_frontend.presentation.screen.login.LoginViewModel

/**
 * SettingsViewModel - Manages settings state and user actions.
 * Persists settings using DataStore Preferences
 */
class SettingsViewModel(
    private val settingsDataStore: ISettingsDataStore,
    private val loginUseCase: LoginUseCase? = null,
    private val loginViewModel: LoginViewModel? = null
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
     * Triggers the login flow by clearing the session and redirecting to login screen
     */
    fun signOut() {
        viewModelScope.launch {
            _isSigningOut.value = true
            try {
                loginUseCase?.signOut()
                // Reset auth state to trigger immediate navigation to LoginScreen
                loginViewModel?.resetAuthState()
            } catch (e: Exception) {
                // Log error but still set signing out to false
                // The UI will remain in the signed-in state on error
            } finally {
                _isSigningOut.value = false
            }
        }
    }
}

/**
 * Factory for creating SettingsViewModel with SettingsDataStore dependency
 */
class SettingsViewModelFactory(
    private val settingsDataStore: SettingsDataStore,
    private val loginUseCase: LoginUseCase? = null,
    private val loginViewModel: LoginViewModel? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsDataStore, loginUseCase, loginViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package org.ilerna.song_swipe_frontend.presentation.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ilerna.song_swipe_frontend.core.auth.SpotifyTokenHolder
import org.ilerna.song_swipe_frontend.core.config.AppConfig
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.AuthState
import org.ilerna.song_swipe_frontend.domain.model.UserProfileState
import org.ilerna.song_swipe_frontend.domain.usecase.LoginUseCase
import org.ilerna.song_swipe_frontend.domain.usecase.user.GetSpotifyUserProfileUseCase

/**
 * ViewModel for handling login screen state and business logic
 * Updated to support Supabase OAuth flow and Spotify profile fetching
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val getSpotifyUserProfileUseCase: GetSpotifyUserProfileUseCase? = null
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _userProfileState = MutableStateFlow<UserProfileState>(UserProfileState.Idle)
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()

    init {
        // Check for existing session on initialization
        checkExistingSession()
    }

    /**
     * Checks if there's an existing session and updates the state accordingly.
     * Also ensures the Spotify token is available, attempting to refresh if needed.
     */
    private fun checkExistingSession() {
        viewModelScope.launch {
            try {
                // Wait for Supabase to finish initialization and load session from storage
                loginUseCase.awaitInitialization()

                if (loginUseCase.hasActiveSession()) {
                    val user = loginUseCase.getCurrentUser()
                    if (user != null) {
                        _authState.value = AuthState.Success(user.id)
                        
                        // Check if we have a valid Spotify token
                        ensureSpotifyTokenAvailable()
                        
                        // Fetch Spotify profile after successful auth
                        fetchSpotifyUserProfile()
                    } else {
                        _authState.value = AuthState.Idle
                    }
                } else {
                    _authState.value = AuthState.Idle
                }
            } catch (e: Exception) {
                Log.e(AppConfig.LOG_TAG, "Error checking existing session", e)
                _authState.value = AuthState.Idle
            }
        }
    }
    
    /**
     * Ensures a valid Spotify token is available.
     * If the token is missing or expired, attempts to refresh it.
     */
    private suspend fun ensureSpotifyTokenAvailable() {
        val currentToken = SpotifyTokenHolder.getAccessToken()
        
        if (currentToken.isNullOrEmpty()) {
            Log.d(AppConfig.LOG_TAG, "LoginViewModel: Spotify token missing, attempting to refresh...")
            
            // Try to get a fresh token by refreshing the Supabase session
            val freshToken = loginUseCase.refreshSpotifyToken()
            
            if (freshToken.isNullOrEmpty()) {
                Log.w(AppConfig.LOG_TAG, "LoginViewModel: Could not obtain Spotify token. User may need to re-authenticate.")
                // Note: We don't force logout here - the user can still see the app
                // but API calls will fail and the interceptor will handle 401s
            } else {
                Log.d(AppConfig.LOG_TAG, "LoginViewModel: Successfully refreshed Spotify token")
            }
        } else {
            Log.d(AppConfig.LOG_TAG, "LoginViewModel: Spotify token already available")
        }
    }

    /**
     * Initiates the Spotify login flow via Supabase
     * Supabase will automatically open the browser
     */
    fun initiateLogin() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                loginUseCase.initiateLogin()
                // Browser opens automatically, state will update on callback
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Handles the authentication callback from Supabase
     * @param url The deep link URL containing session tokens
     */
    fun handleAuthCallback(url: String) {
        viewModelScope.launch {
            try {
                val result = loginUseCase.handleAuthResponse(url)
                _authState.value = result
                
                // If authentication was successful, fetch Spotify profile
                if (result is AuthState.Success) {
                    fetchSpotifyUserProfile()
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    /**
     * Fetches the user's Spotify profile after successful authentication
     * Updates userProfileState with the result
     */
    private fun fetchSpotifyUserProfile() {
        // Only fetch if use case is available
        val useCase = getSpotifyUserProfileUseCase ?: return
        
        viewModelScope.launch {
            _userProfileState.value = UserProfileState.Loading
            
            when (val result = useCase()) {
                is NetworkResult.Success -> {
                    _userProfileState.value = UserProfileState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _userProfileState.value = UserProfileState.Error(
                        result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // Already set to loading above
                }
            }
        }
    }

    /**
     * Resets the authentication state
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
        _userProfileState.value = UserProfileState.Idle
    }
}
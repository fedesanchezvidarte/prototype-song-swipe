package org.ilerna.song_swipe_frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.ilerna.song_swipe_frontend.core.auth.SpotifyTokenHolder
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISpotifyTokenDataStore
import org.ilerna.song_swipe_frontend.domain.model.AuthState
import org.ilerna.song_swipe_frontend.domain.model.UserProfileState
import org.ilerna.song_swipe_frontend.presentation.screen.login.LoginScreen
import org.ilerna.song_swipe_frontend.presentation.screen.login.LoginViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.main.AppScaffold
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.swipe.SwipeViewModel
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var spotifyTokenDataStore: ISpotifyTokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SpotifyTokenHolder with injected DataStore
        // Using runBlocking to ensure tokens are loaded before any API calls.
        // This prevents race conditions where SpotifyAuthInterceptor might be
        // called before tokens are restored from DataStore.
        // Note: initialize() is idempotent, safe to call on configuration changes.
        SpotifyTokenHolder.initialize(spotifyTokenDataStore)
        runBlocking {
            val loaded = SpotifyTokenHolder.loadFromDataStore()
            if (!loaded) {
                Log.w("MainActivity", "Failed to load Spotify tokens from DataStore")
            }
        }

        setContent {
            // Get ViewModels using Hilt
            val loginViewModel: LoginViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val swipeViewModel: SwipeViewModel = hiltViewModel()
            
            val authState by loginViewModel.authState.collectAsState()
            val userProfileState by loginViewModel.userProfileState.collectAsState()
            val currentTheme by settingsViewModel.currentTheme.collectAsState()
            
            // Handle sign-out event from SettingsViewModel
            LaunchedEffect(Unit) {
                settingsViewModel.signOutComplete.collect {
                    loginViewModel.resetAuthState()
                }
            }
            
            // Handle OAuth callback intent
            LaunchedEffect(intent) {
                handleIntent(intent, loginViewModel)
            }
            
            // Extract user from profile state if available
            val user = (userProfileState as? UserProfileState.Success)?.user

            SongSwipeTheme(themeMode = currentTheme) {
                when (authState) {
                    is AuthState.Success -> {
                        // User is logged in, show main app
                        AppScaffold(
                            user = user,
                            settingsViewModel = settingsViewModel,
                            swipeViewModel = swipeViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        // Show login screen for Idle, Loading, and Error states
                        LoginScreen(
                            authState = authState,
                            onLoginClick = { loginViewModel.initiateLogin() },
                            onResetState = { loginViewModel.resetAuthState() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent so LaunchedEffect can observe it
        // Also handle immediately for cases where setContent hasn't recomposed yet
        lifecycleScope.launch {
            // Get the ViewModel from the activity's ViewModelStore
            // This will be handled by the LaunchedEffect on next recomposition
        }
    }

    private suspend fun handleIntent(intent: Intent?, loginViewModel: LoginViewModel) {
        val uri = intent?.data
        if (uri != null) {
            loginViewModel.handleAuthCallback(uri.toString())
        }
    }
}
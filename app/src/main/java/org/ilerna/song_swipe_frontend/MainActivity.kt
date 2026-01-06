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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.ilerna.song_swipe_frontend.core.auth.SpotifyTokenHolder
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISpotifyTokenDataStore
import org.ilerna.song_swipe_frontend.domain.model.AuthState
import org.ilerna.song_swipe_frontend.domain.model.UserProfileState
import org.ilerna.song_swipe_frontend.domain.usecase.category.GetCategoryPlaylistsUseCase
import org.ilerna.song_swipe_frontend.domain.usecase.category.GetFeaturedPlaylistsUseCase
import org.ilerna.song_swipe_frontend.presentation.screen.login.LoginScreen
import org.ilerna.song_swipe_frontend.presentation.screen.login.LoginViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.main.AppScaffold
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModel
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var spotifyTokenDataStore: ISpotifyTokenDataStore

    @Inject
    lateinit var getCategoryPlaylistsUseCase: GetCategoryPlaylistsUseCase

    @Inject
    lateinit var getFeaturedPlaylistsUseCase: GetFeaturedPlaylistsUseCase
    
    // Track the last processed URI to avoid re-processing on recomposition
    private var lastProcessedUri: String? = null

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
        
        // Handle initial intent if it contains auth callback
        val initialUri = intent?.data?.toString()

        setContent {
            // Get ViewModels using Hilt
            val loginViewModel: LoginViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            
            val authState by loginViewModel.authState.collectAsState()
            val userProfileState by loginViewModel.userProfileState.collectAsState()
            val currentTheme by settingsViewModel.currentTheme.collectAsState()
            
            // Track pending auth callback URI
            var pendingAuthUri by remember { mutableStateOf(initialUri) }
            
            // Handle sign-out event from SettingsViewModel
            LaunchedEffect(Unit) {
                settingsViewModel.signOutComplete.collect {
                    loginViewModel.resetAuthState()
                }
            }
            
            // Handle OAuth callback - only process if we have a new URI
            LaunchedEffect(pendingAuthUri) {
                val uri = pendingAuthUri
                if (uri != null && uri != lastProcessedUri && uri.contains("songswipe://callback")) {
                    Log.d("MainActivity", "Processing auth callback: $uri")
                    lastProcessedUri = uri
                    pendingAuthUri = null // Clear after processing
                    loginViewModel.handleAuthCallback(uri)
                }
            }
            
            // Extract user from profile state if available
            val user = (userProfileState as? UserProfileState.Success)?.user

            SongSwipeTheme(themeMode = currentTheme) {
                when (authState) {
                    is AuthState.Success -> {
                        // User is logged in, show main app with NavController
                        AppScaffold(
                            user = user,
                            settingsViewModel = settingsViewModel,
                            getCategoryPlaylistsUseCase = getCategoryPlaylistsUseCase,
                            getFeaturedPlaylistsUseCase = getFeaturedPlaylistsUseCase,
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
        setIntent(intent)
        
        // Extract URI and trigger recomposition by recreating content
        val uri = intent.data?.toString()
        if (uri != null && uri.contains("songswipe://callback") && uri != lastProcessedUri) {
            Log.d("MainActivity", "onNewIntent received auth callback: $uri")
            // Force recreate to handle the new intent
            recreate()
        }
    }
}
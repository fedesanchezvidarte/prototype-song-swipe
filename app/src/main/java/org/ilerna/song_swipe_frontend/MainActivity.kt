package org.ilerna.song_swipe_frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.ilerna.song_swipe_frontend.core.auth.SpotifyTokenHolder
import org.ilerna.song_swipe_frontend.core.network.interceptors.SpotifyAuthInterceptor
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.SettingsDataStore
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.SpotifyTokenDataStore
import org.ilerna.song_swipe_frontend.data.datasource.remote.api.SpotifyApi
import org.ilerna.song_swipe_frontend.data.datasource.remote.impl.SpotifyDataSourceImpl
import org.ilerna.song_swipe_frontend.data.repository.impl.SpotifyRepositoryImpl
import org.ilerna.song_swipe_frontend.data.repository.impl.SupabaseAuthRepository
import org.ilerna.song_swipe_frontend.domain.usecase.LoginUseCase
import org.ilerna.song_swipe_frontend.domain.model.AuthState
import org.ilerna.song_swipe_frontend.domain.model.UserProfileState
import org.ilerna.song_swipe_frontend.domain.usecase.user.GetSpotifyUserProfileUseCase
import org.ilerna.song_swipe_frontend.presentation.screen.login.LoginScreen
import org.ilerna.song_swipe_frontend.presentation.screen.login.LoginViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.main.AppScaffold
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModelFactory
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var spotifyTokenDataStore: SpotifyTokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TODO: Refactor dependency injection using Hilt
        //       Current manual DI works but doesn't scale well.
        //       See: di/ folder structure in arquitectura docs
        //       Priority: High (critical for maintainability and testing)
        
        // Settings DataStore
        settingsDataStore = SettingsDataStore(applicationContext)
        
        // Spotify Token DataStore - initialize holder and load persisted tokens
        // Using runBlocking to ensure tokens are loaded before any API calls.
        // This prevents race conditions where SpotifyAuthInterceptor might be
        // called before tokens are restored from DataStore.
        // Note: initialize() is idempotent, safe to call on configuration changes.
        spotifyTokenDataStore = SpotifyTokenDataStore(applicationContext)
        SpotifyTokenHolder.initialize(spotifyTokenDataStore)
        runBlocking {
            val loaded = SpotifyTokenHolder.loadFromDataStore()
            if (!loaded) {
                Log.w("MainActivity", "Failed to load Spotify tokens from DataStore")
            }
        }
        
        // Auth dependencies
        val authRepository = SupabaseAuthRepository()
        val loginUseCase = LoginUseCase(authRepository)
        
        // Spotify API dependencies
        val spotifyAuthInterceptor = SpotifyAuthInterceptor()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(spotifyAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        val spotifyApi = retrofit.create(SpotifyApi::class.java)
        val spotifyDataSource = SpotifyDataSourceImpl(spotifyApi)
        val spotifyRepository = SpotifyRepositoryImpl(spotifyDataSource)
        val getSpotifyUserProfileUseCase = GetSpotifyUserProfileUseCase(spotifyRepository)
        
        // Create ViewModel with all dependencies
        loginViewModel = LoginViewModel(loginUseCase, getSpotifyUserProfileUseCase)
        
        // Create SettingsViewModel with LoginViewModel for sign-out functionality
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(settingsDataStore, loginUseCase, loginViewModel)
        )[SettingsViewModel::class.java]

        // Check if we're being called back from Supabase OAuth
        handleIntent(intent)

        setContent {
            val authState by loginViewModel.authState.collectAsState()
            val userProfileState by loginViewModel.userProfileState.collectAsState()
            val currentTheme by settingsViewModel.currentTheme.collectAsState()
            
            // Extract user from profile state if available
            val user = (userProfileState as? UserProfileState.Success)?.user

            SongSwipeTheme(themeMode = currentTheme) {
                when (authState) {
                    is AuthState.Success -> {
                        // User is logged in, show main app
                        AppScaffold(
                            user = user,
                            settingsViewModel = settingsViewModel,
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
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data
        if (uri != null) {
            lifecycleScope.launch {
                loginViewModel.handleAuthCallback(uri.toString())
            }
        }
    }
}
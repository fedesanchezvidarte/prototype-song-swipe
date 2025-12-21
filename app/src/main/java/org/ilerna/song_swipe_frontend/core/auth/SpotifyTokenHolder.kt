package org.ilerna.song_swipe_frontend.core.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISpotifyTokenDataStore

/**
 * Thread-safe holder for the Spotify provider token with DataStore persistence
 * 
 * This is necessary because Supabase's importAuthToken() does not persist
 * the provider_token (Spotify token) in the session. We need to extract it
 * from the OAuth callback URL and store it separately.
 * 
 * This implementation uses DataStore for persistent storage while maintaining
 * an in-memory cache for synchronous access. The cache is synced with DataStore
 * on initialization and updates.
 * 
 * Usage:
 * 1. Call initialize() with a SpotifyTokenDataStore instance on app startup
 * 2. Use setTokens() to store tokens (persists to DataStore)
 * 3. Use getAccessToken()/getRefreshToken() for synchronous cached access
 * 4. Use accessTokenFlow/refreshTokenFlow for reactive updates
 * 
 * @see <a href="https://developer.android.com/topic/security/data">Android Security Best Practices</a>
 */
object SpotifyTokenHolder {
    
    private var tokenDataStore: ISpotifyTokenDataStore? = null
    
    // In-memory cache for synchronous access
    private val _accessTokenFlow = MutableStateFlow<String?>(null)
    private val _refreshTokenFlow = MutableStateFlow<String?>(null)
    
    /**
     * Flow that emits the current Spotify access token
     */
    val accessTokenFlow: Flow<String?> = _accessTokenFlow.asStateFlow()
    
    /**
     * Flow that emits the current Spotify refresh token
     */
    val refreshTokenFlow: Flow<String?> = _refreshTokenFlow.asStateFlow()
    
    /**
     * Initializes the token holder with a DataStore instance
     * Should be called once during app startup
     * 
     * @param dataStore The SpotifyTokenDataStore instance for persistence
     */
    fun initialize(dataStore: ISpotifyTokenDataStore) {
        tokenDataStore = dataStore
    }
    
    /**
     * Loads tokens from DataStore into memory cache
     * Should be called after initialize() to restore persisted tokens
     */
    suspend fun loadFromDataStore() {
        tokenDataStore?.let { store ->
            _accessTokenFlow.value = store.getAccessTokenSync()
            _refreshTokenFlow.value = store.getRefreshTokenSync()
        }
    }
    
    /**
     * Stores the Spotify tokens extracted from OAuth callback
     * Persists to DataStore and updates in-memory cache
     */
    suspend fun setTokens(accessToken: String?, refreshToken: String?) {
        _accessTokenFlow.value = accessToken
        _refreshTokenFlow.value = refreshToken
        tokenDataStore?.setTokens(accessToken, refreshToken)
    }
    
    /**
     * Gets the current Spotify access token from cache
     * @return The Spotify access token or null if not available
     */
    fun getAccessToken(): String? = _accessTokenFlow.value
    
    /**
     * Gets the current Spotify refresh token from cache
     * @return The Spotify refresh token or null if not available
     */
    fun getRefreshToken(): String? = _refreshTokenFlow.value
    
    /**
     * Clears all stored tokens (call on logout)
     * Clears both in-memory cache and persisted DataStore
     */
    suspend fun clear() {
        _accessTokenFlow.value = null
        _refreshTokenFlow.value = null
        tokenDataStore?.clear()
    }
    
    /**
     * Checks if a valid Spotify token is available in cache
     */
    fun hasToken(): Boolean = !_accessTokenFlow.value.isNullOrEmpty()
}

package org.ilerna.song_swipe_frontend.domain.repository

import org.ilerna.song_swipe_frontend.domain.model.AuthState
import org.ilerna.song_swipe_frontend.domain.model.User

/**
 * Repository interface for authentication operations
 * Updated to support Supabase OAuth flow
 */
interface AuthRepository {

    /**
     * Initiates Spotify login via Supabase OAuth
     * @return The OAuth URL to open in browser
     */
    suspend fun initiateSpotifyLogin()

    /**
     * Handles the OAuth callback and imports the session
     * @param url The deep link URL containing session tokens
     * @return AuthState representing the result
     */
    suspend fun handleAuthCallback(url: String): AuthState

    /**
     * Gets the current authenticated user
     * @return User if authenticated, null otherwise
     */
    suspend fun getCurrentUser(): User?

    /**
     * Gets the Spotify access token from the current session
     * @return Spotify access token if available, null otherwise
     */
    suspend fun getSpotifyAccessToken(): String?

    /**
     * Signs out the current user
     */
    suspend fun signOut()

    /**
     * Checks if user has an active session
     * @return true if session is active, false otherwise
     */
    suspend fun hasActiveSession(): Boolean
    
    /**
     * Refreshes the Supabase session and attempts to get a fresh Spotify provider token.
     * This should be called when the Spotify token is expired or missing.
     * 
     * Note: Supabase may not return a provider_token on refresh if it wasn't originally
     * requested with the proper scopes or if the OAuth provider doesn't support it.
     * In such cases, the user may need to re-authenticate.
     * 
     * @return The fresh Spotify access token if available, null if refresh failed or token unavailable
     */
    suspend fun refreshSpotifyToken(): String?
}
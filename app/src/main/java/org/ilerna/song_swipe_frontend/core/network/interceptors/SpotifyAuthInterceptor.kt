package org.ilerna.song_swipe_frontend.core.network.interceptors

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.ilerna.song_swipe_frontend.core.auth.SpotifyTokenHolder
import org.ilerna.song_swipe_frontend.core.config.AppConfig
import org.ilerna.song_swipe_frontend.domain.repository.AuthRepository

/**
 * OkHttp interceptor that automatically injects the Spotify authentication token
 * into all HTTP requests to the Spotify API.
 * 
 * This interceptor runs before each request, adding the Authorization header
 * (Bearer token) needed to authenticate with Spotify Web API.
 * 
 * The token is retrieved from SpotifyTokenHolder, which stores the provider_token
 * extracted from the OAuth callback URL.
 * 
 * If the initial request fails with 401, the interceptor will attempt to refresh
 * the token by calling the AuthRepository and retry the request once.
 */
class SpotifyAuthInterceptor(
    private val authRepository: AuthRepository? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get Spotify token from holder
        var spotifyToken = SpotifyTokenHolder.getAccessToken()
        
        // If no token available, proceed without modifying the request
        if (spotifyToken.isNullOrEmpty()) {
            Log.w(AppConfig.LOG_TAG, "SpotifyAuthInterceptor: No token available")
            return chain.proceed(originalRequest)
        }
        
        Log.d(AppConfig.LOG_TAG, "SpotifyAuthInterceptor: Adding Bearer token to request")
        
        // Add authorization header
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $spotifyToken")
            .build()
        
        val response = chain.proceed(authenticatedRequest)
        
        // If we get a 401 Unauthorized, try to refresh the token and retry once
        if (response.code == 401 && authRepository != null) {
            Log.w(AppConfig.LOG_TAG, "SpotifyAuthInterceptor: Got 401, attempting token refresh...")
            
            response.close() // Close the failed response
            
            // Try to refresh the token
            val freshToken = runBlocking {
                try {
                    authRepository.refreshSpotifyToken()
                } catch (e: Exception) {
                    Log.e(AppConfig.LOG_TAG, "SpotifyAuthInterceptor: Token refresh failed", e)
                    null
                }
            }
            
            if (!freshToken.isNullOrEmpty()) {
                Log.d(AppConfig.LOG_TAG, "SpotifyAuthInterceptor: Token refreshed, retrying request")
                
                // Retry with the fresh token
                val retryRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $freshToken")
                    .build()
                
                return chain.proceed(retryRequest)
            } else {
                Log.w(AppConfig.LOG_TAG, "SpotifyAuthInterceptor: Could not refresh token, user may need to re-authenticate")
                // Return the original 401 response - the app should handle this and prompt re-login
                return chain.proceed(authenticatedRequest)
            }
        }
        
        return response
    }
}

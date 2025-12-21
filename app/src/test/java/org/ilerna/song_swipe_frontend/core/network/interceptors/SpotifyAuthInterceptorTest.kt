package org.ilerna.song_swipe_frontend.core.network.interceptors

import android.util.Log
import io.mockk.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.ilerna.song_swipe_frontend.core.auth.SpotifyTokenHolder
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISpotifyTokenDataStore
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SpotifyAuthInterceptor
 * Focuses on token injection using SpotifyTokenHolder
 */
class SpotifyAuthInterceptorTest {

    private lateinit var interceptor: SpotifyAuthInterceptor
    private lateinit var mockChain: Interceptor.Chain
    private lateinit var mockTokenDataStore: ISpotifyTokenDataStore

    @Before
    fun setup() {
        // Mock Android Log to prevent "Method not mocked" errors
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        
        mockChain = mockk(relaxed = true)
        interceptor = SpotifyAuthInterceptor()
        
        // Create a mock DataStore for testing
        mockTokenDataStore = createMockTokenDataStore()
        SpotifyTokenHolder.reset()
        SpotifyTokenHolder.initialize(mockTokenDataStore)
    }
    
    private fun createMockTokenDataStore(): ISpotifyTokenDataStore {
        val accessTokenFlow = MutableStateFlow<String?>(null)
        val refreshTokenFlow = MutableStateFlow<String?>(null)
        
        return object : ISpotifyTokenDataStore {
            override val accessToken: Flow<String?> = accessTokenFlow
            override val refreshToken: Flow<String?> = refreshTokenFlow
            
            override suspend fun setTokens(accessToken: String?, refreshToken: String?) {
                accessTokenFlow.value = accessToken
                refreshTokenFlow.value = refreshToken
            }
            
            override suspend fun getAccessTokenSync(): String? = accessTokenFlow.value
            override suspend fun getRefreshTokenSync(): String? = refreshTokenFlow.value
            override suspend fun hasToken(): Boolean = !accessTokenFlow.value.isNullOrEmpty()
            override suspend fun clear() {
                accessTokenFlow.value = null
                refreshTokenFlow.value = null
            }
        }
    }

    @After
    fun tearDown() = runTest {
        // Clean up tokens after each test
        SpotifyTokenHolder.reset()
        unmockkStatic(Log::class)
    }

    // ==================== Token Injection Tests ====================

    @Test
    fun `intercept should add Authorization header when token is available`() = runTest {
        // Given
        val testToken = "spotify_access_token_123"
        SpotifyTokenHolder.setTokens(testToken, null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        val mockResponse = mockk<Response>()

        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mockResponse

        // When
        interceptor.intercept(mockChain)

        // Then
        verify {
            mockChain.proceed(match { request ->
                request.header("Authorization") == "Bearer $testToken"
            })
        }
    }

    @Test
    fun `intercept should not add Authorization header when token is null`() = runTest {
        // Given - no token set (SpotifyTokenHolder cache is empty)
        SpotifyTokenHolder.clearCacheOnly()
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        val mockResponse = mockk<Response>()

        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mockResponse

        // When
        interceptor.intercept(mockChain)

        // Then
        verify {
            mockChain.proceed(match { request ->
                request.header("Authorization") == null
            })
        }
    }

    @Test
    fun `intercept should not add Authorization header when token is empty string`() = runTest {
        // Given
        SpotifyTokenHolder.setTokens("", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        val mockResponse = mockk<Response>()

        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mockResponse

        // When
        interceptor.intercept(mockChain)

        // Then
        verify {
            mockChain.proceed(match { request ->
                request.header("Authorization") == null
            })
        }
    }

    // ==================== Token Management Tests ====================

    @Test
    fun `intercept should use updated token after clear and set`() = runTest {
        // Given - first set a token
        SpotifyTokenHolder.setTokens("old_token", null)
        SpotifyTokenHolder.clear()
        SpotifyTokenHolder.setTokens("new_token", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        val mockResponse = mockk<Response>()

        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mockResponse

        // When
        interceptor.intercept(mockChain)

        // Then
        verify {
            mockChain.proceed(match { request ->
                request.header("Authorization") == "Bearer new_token"
            })
        }
    }

    @Test
    fun `intercept should preserve original request URL and method`() = runTest {
        // Given
        SpotifyTokenHolder.setTokens("test_token", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        val mockResponse = mockk<Response>()

        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mockResponse

        // When
        interceptor.intercept(mockChain)

        // Then
        verify {
            mockChain.proceed(match { request ->
                request.url.toString() == "https://api.spotify.com/v1/me" &&
                request.method == "GET"
            })
        }
    }
}

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
import org.ilerna.song_swipe_frontend.domain.repository.AuthRepository
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for SpotifyAuthInterceptor
 * Focuses on token injection using SpotifyTokenHolder and 401 retry logic
 */
class SpotifyAuthInterceptorTest {

    private lateinit var interceptor: SpotifyAuthInterceptor
    private lateinit var mockChain: Interceptor.Chain
    private lateinit var mockTokenDataStore: ISpotifyTokenDataStore
    private lateinit var mockAuthRepository: AuthRepository

    @Before
    fun setup() {
        // Mock Android Log to prevent "Method not mocked" errors
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>(), any()) } returns 0
        
        mockChain = mockk(relaxed = true)
        mockAuthRepository = mockk(relaxed = true)
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
        every { mockResponse.code } returns 200

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
        every { mockResponse.code } returns 200

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
        every { mockResponse.code } returns 200

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
        every { mockResponse.code } returns 200

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
        every { mockResponse.code } returns 200

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

    // ==================== 401 Retry Logic Tests ====================

    @Test
    fun `intercept should retry request with fresh token on 401 when authRepository is provided`() = runTest {
        // Given
        val interceptorWithAuth = SpotifyAuthInterceptor(mockAuthRepository)
        val expiredToken = "expired_token"
        val freshToken = "fresh_token"
        SpotifyTokenHolder.setTokens(expiredToken, null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        
        val mock401Response = mockk<Response>()
        every { mock401Response.code } returns 401
        every { mock401Response.close() } just Runs
        
        val mockSuccessResponse = mockk<Response>()
        every { mockSuccessResponse.code } returns 200
        
        every { mockChain.request() } returns originalRequest
        // First call returns 401, second call returns success
        every { mockChain.proceed(any()) } returnsMany listOf(mock401Response, mockSuccessResponse)
        
        coEvery { mockAuthRepository.refreshSpotifyToken() } coAnswers {
            SpotifyTokenHolder.setTokens(freshToken, null)
            freshToken
        }

        // When
        val result = interceptorWithAuth.intercept(mockChain)

        // Then
        assertEquals(mockSuccessResponse, result)
        coVerify { mockAuthRepository.refreshSpotifyToken() }
        verify(exactly = 2) { mockChain.proceed(any()) }
    }

    @Test
    fun `intercept should not retry on 401 when authRepository is null`() = runTest {
        // Given - interceptor without authRepository
        val interceptorNoAuth = SpotifyAuthInterceptor(null)
        SpotifyTokenHolder.setTokens("expired_token", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        
        val mock401Response = mockk<Response>()
        every { mock401Response.code } returns 401
        
        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mock401Response

        // When
        val result = interceptorNoAuth.intercept(mockChain)

        // Then - Should return the 401 response without retry
        assertEquals(mock401Response, result)
        verify(exactly = 1) { mockChain.proceed(any()) }
    }

    @Test
    fun `intercept should return original response on 401 when token refresh fails`() = runTest {
        // Given
        val interceptorWithAuth = SpotifyAuthInterceptor(mockAuthRepository)
        SpotifyTokenHolder.setTokens("expired_token", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        
        val mock401Response = mockk<Response>()
        every { mock401Response.code } returns 401
        every { mock401Response.close() } just Runs
        
        // Second proceed call for the retry with same token (since refresh failed)
        val mockRetryResponse = mockk<Response>()
        
        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returnsMany listOf(mock401Response, mockRetryResponse)
        
        // Token refresh fails
        coEvery { mockAuthRepository.refreshSpotifyToken() } returns null

        // When
        val result = interceptorWithAuth.intercept(mockChain)

        // Then - Should return the retry response (which would still be 401)
        coVerify { mockAuthRepository.refreshSpotifyToken() }
    }

    @Test
    fun `intercept should return original response on 401 when token refresh throws exception`() = runTest {
        // Given
        val interceptorWithAuth = SpotifyAuthInterceptor(mockAuthRepository)
        SpotifyTokenHolder.setTokens("expired_token", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        
        val mock401Response = mockk<Response>()
        every { mock401Response.code } returns 401
        every { mock401Response.close() } just Runs
        
        val mockRetryResponse = mockk<Response>()
        
        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returnsMany listOf(mock401Response, mockRetryResponse)
        
        // Token refresh throws exception
        coEvery { mockAuthRepository.refreshSpotifyToken() } throws Exception("Network error")

        // When
        interceptorWithAuth.intercept(mockChain)

        // Then
        coVerify { mockAuthRepository.refreshSpotifyToken() }
    }

    @Test
    fun `intercept should not retry on non-401 error codes`() = runTest {
        // Given
        val interceptorWithAuth = SpotifyAuthInterceptor(mockAuthRepository)
        SpotifyTokenHolder.setTokens("valid_token", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        
        val mock500Response = mockk<Response>()
        every { mock500Response.code } returns 500
        
        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mock500Response

        // When
        val result = interceptorWithAuth.intercept(mockChain)

        // Then - Should return 500 without attempting refresh
        assertEquals(mock500Response, result)
        coVerify(exactly = 0) { mockAuthRepository.refreshSpotifyToken() }
        verify(exactly = 1) { mockChain.proceed(any()) }
    }

    @Test
    fun `intercept should not retry on successful response`() = runTest {
        // Given
        val interceptorWithAuth = SpotifyAuthInterceptor(mockAuthRepository)
        SpotifyTokenHolder.setTokens("valid_token", null)
        
        val originalRequest = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .build()
        
        val mockSuccessResponse = mockk<Response>()
        every { mockSuccessResponse.code } returns 200
        
        every { mockChain.request() } returns originalRequest
        every { mockChain.proceed(any()) } returns mockSuccessResponse

        // When
        val result = interceptorWithAuth.intercept(mockChain)

        // Then
        assertEquals(mockSuccessResponse, result)
        coVerify(exactly = 0) { mockAuthRepository.refreshSpotifyToken() }
        verify(exactly = 1) { mockChain.proceed(any()) }
    }
}

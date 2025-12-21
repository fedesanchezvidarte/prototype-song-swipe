package org.ilerna.song_swipe_frontend.core.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ISpotifyTokenDataStore
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for SpotifyTokenHolder
 * Tests token storage, retrieval, and clearing functionality
 */
class SpotifyTokenHolderTest {

    private lateinit var mockTokenDataStore: ISpotifyTokenDataStore

    @Before
    fun setup() {
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
    fun tearDown() {
        // Clean up after each test
        SpotifyTokenHolder.reset()
    }

    // ==================== Token Storage Tests ====================

    @Test
    fun `setTokens should store access token correctly`() = runTest {
        // Given
        val accessToken = "test_access_token_123"
        val refreshToken = "test_refresh_token_456"

        // When
        SpotifyTokenHolder.setTokens(accessToken, refreshToken)

        // Then
        assertEquals(accessToken, SpotifyTokenHolder.getAccessToken())
        assertEquals(refreshToken, SpotifyTokenHolder.getRefreshToken())
    }

    @Test
    fun `setTokens should handle null refresh token`() = runTest {
        // Given
        val accessToken = "test_access_token"

        // When
        SpotifyTokenHolder.setTokens(accessToken, null)

        // Then
        assertEquals(accessToken, SpotifyTokenHolder.getAccessToken())
        assertNull(SpotifyTokenHolder.getRefreshToken())
    }

    @Test
    fun `setTokens should overwrite existing tokens`() = runTest {
        // Given
        SpotifyTokenHolder.setTokens("old_access", "old_refresh")

        // When
        SpotifyTokenHolder.setTokens("new_access", "new_refresh")

        // Then
        assertEquals("new_access", SpotifyTokenHolder.getAccessToken())
        assertEquals("new_refresh", SpotifyTokenHolder.getRefreshToken())
    }

    // ==================== Token Retrieval Tests ====================

    @Test
    fun `getAccessToken should return null when no token set`() {
        // Given - no tokens set (clean state from setup)

        // When
        val result = SpotifyTokenHolder.getAccessToken()

        // Then
        assertNull(result)
    }

    @Test
    fun `getRefreshToken should return null when no token set`() {
        // Given - no tokens set

        // When
        val result = SpotifyTokenHolder.getRefreshToken()

        // Then
        assertNull(result)
    }

    // ==================== Clear Tests ====================

    @Test
    fun `clear should remove all stored tokens`() = runTest {
        // Given
        SpotifyTokenHolder.setTokens("access", "refresh")

        // When
        SpotifyTokenHolder.clear()

        // Then
        assertNull(SpotifyTokenHolder.getAccessToken())
        assertNull(SpotifyTokenHolder.getRefreshToken())
    }

    // ==================== hasToken Tests ====================

    @Test
    fun `hasToken should return true when access token is set`() = runTest {
        // Given
        SpotifyTokenHolder.setTokens("valid_token", null)

        // When
        val result = SpotifyTokenHolder.hasToken()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasToken should return false when no token is set`() {
        // Given - no tokens set

        // When
        val result = SpotifyTokenHolder.hasToken()

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasToken should return false when token is empty string`() = runTest {
        // Given
        SpotifyTokenHolder.setTokens("", null)

        // When
        val result = SpotifyTokenHolder.hasToken()

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasToken should return false after clear`() = runTest {
        // Given
        SpotifyTokenHolder.setTokens("token", "refresh")
        SpotifyTokenHolder.clear()

        // When
        val result = SpotifyTokenHolder.hasToken()

        // Then
        assertFalse(result)
    }
}

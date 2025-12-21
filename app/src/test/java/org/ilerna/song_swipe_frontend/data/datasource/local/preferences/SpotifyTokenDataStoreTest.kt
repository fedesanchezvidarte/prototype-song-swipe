package org.ilerna.song_swipe_frontend.data.datasource.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for SpotifyTokenDataStore
 * Tests token persistence, retrieval, and clearing functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SpotifyTokenDataStoreTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private fun createTestDataStore(testScope: TestScope): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = {
                File(
                    tmpFolder.root,
                    "test_spotify_tokens_${UUID.randomUUID()}.preferences_pb"
                )
            }
        )
    }

    @Test
    fun `accessToken returns null as default when no token is set`() =
        runTest(UnconfinedTestDispatcher()) {
            // Given
            val testDataStore = createTestDataStore(this)
            val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)

            // When
            val accessToken = tokenDataStore.accessToken.first()

            // Then
            assertNull(accessToken)
        }

    @Test
    fun `refreshToken returns null as default when no token is set`() =
        runTest(UnconfinedTestDispatcher()) {
            // Given
            val testDataStore = createTestDataStore(this)
            val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)

            // When
            val refreshToken = tokenDataStore.refreshToken.first()

            // Then
            assertNull(refreshToken)
        }

    @Test
    fun `setTokens persists access token correctly`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        val testAccessToken = "spotify_access_token_123"

        // When
        tokenDataStore.setTokens(testAccessToken, null)

        // Then
        val savedToken = tokenDataStore.accessToken.first()
        assertEquals(testAccessToken, savedToken)
    }

    @Test
    fun `setTokens persists refresh token correctly`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        val testRefreshToken = "spotify_refresh_token_456"

        // When
        tokenDataStore.setTokens(null, testRefreshToken)

        // Then
        val savedToken = tokenDataStore.refreshToken.first()
        assertEquals(testRefreshToken, savedToken)
    }

    @Test
    fun `setTokens persists both tokens correctly`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        val testAccessToken = "spotify_access_token_123"
        val testRefreshToken = "spotify_refresh_token_456"

        // When
        tokenDataStore.setTokens(testAccessToken, testRefreshToken)

        // Then
        assertEquals(testAccessToken, tokenDataStore.accessToken.first())
        assertEquals(testRefreshToken, tokenDataStore.refreshToken.first())
    }

    // @Test
    fun `clear removes all tokens`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        tokenDataStore.setTokens("access_token", "refresh_token")

        // Verify tokens were set
        assertEquals("access_token", tokenDataStore.accessToken.first())
        assertEquals("refresh_token", tokenDataStore.refreshToken.first())

        // When
        tokenDataStore.clear()

        // Then
        assertNull(tokenDataStore.accessToken.first())
        assertNull(tokenDataStore.refreshToken.first())
    }

    @Test
    fun `hasToken returns false when no token is set`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)

        // When
        val hasToken = tokenDataStore.hasToken()

        // Then
        assertFalse(hasToken)
    }

    @Test
    fun `hasToken returns true when access token is set`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        tokenDataStore.setTokens("access_token", null)

        // When
        val hasToken = tokenDataStore.hasToken()

        // Then
        assertTrue(hasToken)
    }

    @Test
    fun `hasToken returns false when access token is empty string`() =
        runTest(UnconfinedTestDispatcher()) {
            // Given
            val testDataStore = createTestDataStore(this)
            val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
            tokenDataStore.setTokens("", null)

            // When
            val hasToken = tokenDataStore.hasToken()

            // Then
            assertFalse(hasToken)
        }

    @Test
    fun `getAccessTokenSync returns token synchronously`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        val testToken = "sync_access_token"
        tokenDataStore.setTokens(testToken, null)

        // When
        val token = tokenDataStore.getAccessTokenSync()

        // Then
        assertEquals(testToken, token)
    }

    @Test
    fun `getRefreshTokenSync returns token synchronously`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        val testToken = "sync_refresh_token"
        tokenDataStore.setTokens(null, testToken)

        // When
        val token = tokenDataStore.getRefreshTokenSync()

        // Then
        assertEquals(testToken, token)
    }

    // @Test
    fun `setTokens with null removes existing token`() = runTest(UnconfinedTestDispatcher()) {
        // Given
        val testDataStore = createTestDataStore(this)
        val tokenDataStore = TestableSpotifyTokenDataStore(testDataStore)
        tokenDataStore.setTokens("access_token", "refresh_token")

        // When - set access token to null
        tokenDataStore.setTokens(null, "refresh_token")

        // Then
        assertNull(tokenDataStore.accessToken.first())
        assertEquals("refresh_token", tokenDataStore.refreshToken.first())
    }
}

/**
 * Testable version of SpotifyTokenDataStore that accepts a DataStore directly
 * This allows us to use a test DataStore instead of one tied to Context
 */
class TestableSpotifyTokenDataStore(private val dataStore: DataStore<Preferences>) :
    ISpotifyTokenDataStore {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("spotify_access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("spotify_refresh_token")
    }

    override val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    override val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    override suspend fun setTokens(accessToken: String?, refreshToken: String?) {
        dataStore.edit { preferences ->
            if (accessToken != null) {
                preferences[ACCESS_TOKEN_KEY] = accessToken
            } else {
                preferences.remove(ACCESS_TOKEN_KEY)
            }
            if (refreshToken != null) {
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            } else {
                preferences.remove(REFRESH_TOKEN_KEY)
            }
        }
    }

    override suspend fun getAccessTokenSync(): String? = accessToken.first()

    override suspend fun getRefreshTokenSync(): String? = refreshToken.first()

    override suspend fun hasToken(): Boolean = !getAccessTokenSync().isNullOrEmpty()

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}

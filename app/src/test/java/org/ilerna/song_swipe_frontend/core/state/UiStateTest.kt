package org.ilerna.song_swipe_frontend.core.state

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for UiState sealed class
 * Tests state handling, helper properties, and extension functions
 */
class UiStateTest {

    // ==================== State Construction Tests ====================

    @Test
    fun `Loading should contain optional message`() {
        val stateWithMessage = UiState.Loading("Loading data...")
        val stateWithoutMessage = UiState.Loading()

        assertEquals("Loading data...", stateWithMessage.message)
        assertNull(stateWithoutMessage.message)
    }

    @Test
    fun `Success should contain data`() {
        val state = UiState.Success("test data")
        assertEquals("test data", state.data)
    }

    @Test
    fun `Error should contain message and optional throwable`() {
        val exception = RuntimeException("Test exception")
        val stateWithThrowable = UiState.Error("Error occurred", exception)
        val stateWithoutThrowable = UiState.Error("Error occurred")

        assertEquals("Error occurred", stateWithThrowable.message)
        assertEquals(exception, stateWithThrowable.throwable)
        assertNull(stateWithoutThrowable.throwable)
    }

    // ==================== Helper Property Tests ====================

    @Test
    fun `helper properties should correctly identify state types`() {
        val idle: UiState<String> = UiState.Idle
        val loading: UiState<String> = UiState.Loading()
        val success: UiState<String> = UiState.Success("data")
        val error: UiState<String> = UiState.Error("error")

        assertTrue(idle.isIdle)
        assertTrue(loading.isLoading)
        assertTrue(success.isSuccess)
        assertTrue(error.isError)

        assertFalse(idle.isLoading)
        assertFalse(loading.isSuccess)
        assertFalse(success.isError)
        assertFalse(error.isIdle)
    }

    // ==================== getOrNull / errorOrNull Tests ====================

    @Test
    fun `getOrNull returns data only for Success state`() {
        assertEquals("test data", UiState.Success("test data").getOrNull())
        assertNull((UiState.Idle as UiState<String>).getOrNull())
        assertNull((UiState.Loading() as UiState<String>).getOrNull())
        assertNull((UiState.Error("error") as UiState<String>).getOrNull())
    }

    @Test
    fun `errorOrNull returns message only for Error state`() {
        assertEquals("Something went wrong", UiState.Error("Something went wrong").errorOrNull())
        assertNull((UiState.Idle as UiState<String>).errorOrNull())
        assertNull((UiState.Loading() as UiState<String>).errorOrNull())
        assertNull(UiState.Success("data").errorOrNull())
    }

    // ==================== map Tests ====================

    @Test
    fun `map transforms data in Success state`() {
        val state = UiState.Success(5)
        val mapped = state.map { it * 2 }

        assertTrue(mapped is UiState.Success)
        assertEquals(10, mapped.data)
    }

    @Test
    fun `map preserves non-Success states`() {
        val idle: UiState<Int> = UiState.Idle
        val loading: UiState<Int> = UiState.Loading("Loading...")
        val error: UiState<Int> = UiState.Error("Error")

        assertTrue(idle.map { it * 2 } is UiState.Idle)

        val mappedLoading = loading.map { it * 2 }
        assertTrue(mappedLoading is UiState.Loading)
        assertEquals("Loading...", mappedLoading.message)

        val mappedError = error.map { it * 2 }
        assertTrue(mappedError is UiState.Error)
        assertEquals("Error", mappedError.message)
    }

    // ==================== Callback Tests ====================

    @Test
    fun `onSuccess executes action only for Success state`() {
        var captured: String? = null

        UiState.Success("data").onSuccess { captured = it }
        assertEquals("data", captured)

        captured = null
        (UiState.Idle as UiState<String>).onSuccess { captured = it }
        assertNull(captured)
    }

    @Test
    fun `onError executes action only for Error state`() {
        val exception = RuntimeException("Test")
        var capturedMessage: String? = null
        var capturedThrowable: Throwable? = null

        UiState.Error("Error message", exception).onError { msg, throwable ->
            capturedMessage = msg
            capturedThrowable = throwable
        }

        assertEquals("Error message", capturedMessage)
        assertEquals(exception, capturedThrowable)
    }

    @Test
    fun `onLoading executes action only for Loading state`() {
        var captured: String? = null

        UiState.Loading("Loading...").onLoading { captured = it }
        assertEquals("Loading...", captured)

        captured = null
        (UiState.Idle as UiState<String>).onLoading { captured = it }
        assertNull(captured)
    }

    // ==================== toUiState Extension Tests ====================

    @Test
    fun `Result success converts to UiState Success`() {
        val result = Result.success("data")
        val state = result.toUiState()

        assertTrue(state is UiState.Success)
        assertEquals("data", state.data)
    }

    @Test
    fun `Result failure converts to UiState Error`() {
        val exception = RuntimeException("Something went wrong")
        val result = Result.failure<String>(exception)
        val state = result.toUiState()

        assertTrue(state is UiState.Error)
        assertEquals("Something went wrong", state.message)
        assertEquals(exception, state.throwable)
    }

    @Test
    fun `Result failure with null message uses default`() {
        val exception = RuntimeException()
        val state = Result.failure<String>(exception).toUiState()

        assertTrue(state is UiState.Error)
        assertEquals("Unknown error", state.message)
    }
}

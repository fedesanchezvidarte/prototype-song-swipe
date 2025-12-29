package org.ilerna.song_swipe_frontend.core.state

/**
 * Generic UI state wrapper for handling Loading/Success/Error states consistently.
 * Use this to standardize state management across all screens.
 *
 * @param T The type of data in the Success state
 */
sealed class UiState<out T> {
    
    /**
     * Initial idle state before any action is taken.
     */
    data object Idle : UiState<Nothing>()
    
    /**
     * Loading state while fetching data.
     * @param message Optional loading message to display
     */
    data class Loading(val message: String? = null) : UiState<Nothing>()
    
    /**
     * Success state with data.
     * @param data The loaded data
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Error state with error information.
     * @param message User-friendly error message
     * @param throwable Optional underlying exception for logging
     */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>()
    
    /**
     * Helper properties for easy state checking
     */
    val isIdle: Boolean get() = this is Idle
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    /**
     * Get data if in Success state, null otherwise
     */
    fun getOrNull(): T? = (this as? Success)?.data
    
    /**
     * Get error message if in Error state, null otherwise
     */
    fun errorOrNull(): String? = (this as? Error)?.message
    
    /**
     * Transform the data in Success state
     */
    inline fun <R> map(transform: (T) -> R): UiState<R> {
        return when (this) {
            is Idle -> Idle
            is Loading -> Loading(message)
            is Success -> Success(transform(data))
            is Error -> Error(message, throwable)
        }
    }
    
    /**
     * Execute action only if in Success state
     */
    inline fun onSuccess(action: (T) -> Unit): UiState<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Execute action only if in Error state
     */
    inline fun onError(action: (String, Throwable?) -> Unit): UiState<T> {
        if (this is Error) action(message, throwable)
        return this
    }
    
    /**
     * Execute action only if in Loading state
     */
    inline fun onLoading(action: (String?) -> Unit): UiState<T> {
        if (this is Loading) action(message)
        return this
    }
}

/**
 * Extension function to convert Result to UiState
 */
fun <T> Result<T>.toUiState(): UiState<T> {
    return fold(
        onSuccess = { UiState.Success(it) },
        onFailure = { UiState.Error(it.message ?: "Unknown error", it) }
    )
}

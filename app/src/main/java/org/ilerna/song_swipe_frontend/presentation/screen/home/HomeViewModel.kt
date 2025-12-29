package org.ilerna.song_swipe_frontend.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.ilerna.song_swipe_frontend.core.state.UiState
import org.ilerna.song_swipe_frontend.core.state.toUiState
import org.ilerna.song_swipe_frontend.domain.model.MusicCategory
import org.ilerna.song_swipe_frontend.domain.repository.CategoryRepository
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 * Manages the state of music categories and user interactions.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<MusicCategory>>>(UiState.Idle)
    val categoriesState: StateFlow<UiState<List<MusicCategory>>> = _categoriesState.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Load music categories from the repository.
     */
    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading()
            _categoriesState.value = categoryRepository.getCategories().toUiState()
        }
    }

    /**
     * Refresh categories (pull-to-refresh or retry).
     */
    fun refresh() {
        loadCategories()
    }
}

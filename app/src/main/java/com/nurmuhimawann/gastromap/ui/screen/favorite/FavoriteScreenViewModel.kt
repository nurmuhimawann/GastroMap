package com.nurmuhimawann.gastromap.ui.screen.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurmuhimawann.gastromap.data.RestaurantRepository
import com.nurmuhimawann.gastromap.data.Result
import com.nurmuhimawann.gastromap.data.local.entity.FavoriteRestaurantEntity
import com.nurmuhimawann.gastromap.data.remote.response.RestaurantsItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val result: Result<List<FavoriteRestaurantEntity>> = Result.Loading,
    val isSearchOpen: Boolean = false,
    val searchQuery: String = ""
)

class FavoriteScreenViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> get() = _uiState

    private var currentSearchJob: Job? = null

    init {
        getFavoriteRestaurants()
    }

    fun getFavoriteRestaurants() {
        viewModelScope.launch {
            _uiState.update { it.copy(result = Result.Loading) }
            repository.getFavoriteRestaurants().catch {
                _uiState.update { state -> state.copy(result = Result.Error(it.message.toString())) }
            }.collect { restaurants ->
                _uiState.update { it.copy(result = Result.Success(restaurants)) }
            }
        }
    }

    fun searchFavoriteRestaurants(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        
        currentSearchJob?.cancel()
        
        if (newQuery.isBlank()) {
            getFavoriteRestaurants()
            return
        }

        currentSearchJob = viewModelScope.launch {
            _uiState.update { it.copy(result = Result.Loading) }
            delay(300)
            repository.searchFavoriteRestaurants(newQuery)
                .catch { e ->
                    _uiState.update { it.copy(result = Result.Error(e.message ?: "An error occurred")) }
                }
                .collect { restaurants ->
                    _uiState.update { it.copy(result = Result.Success(restaurants)) }
                }
        }
    }

    fun onToggleFavorite(restaurant: RestaurantsItem) {
        viewModelScope.launch {
            repository.deleteFavoriteRestaurant(restaurant.id)
        }
    }

    fun clearQuery() {
        if (_uiState.value.searchQuery.isBlank()) {
            _uiState.update { it.copy(isSearchOpen = false) }
        } else {
            _uiState.update { it.copy(searchQuery = "") }
            getFavoriteRestaurants()
        }
    }
}

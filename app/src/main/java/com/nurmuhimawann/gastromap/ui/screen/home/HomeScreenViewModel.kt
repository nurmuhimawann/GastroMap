package com.nurmuhimawann.gastromap.ui.screen.home

import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.io.IOException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurmuhimawann.gastromap.data.RestaurantRepository
import com.nurmuhimawann.gastromap.data.local.entity.FavoriteRestaurantEntity
import com.nurmuhimawann.gastromap.data.remote.response.RestaurantsItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RestaurantWithFavorite(
    val restaurant: RestaurantsItem,
    val isFavorite: Boolean
)

data class HomeUiState(
    val featuredRestaurants: List<RestaurantsItem> = emptyList(),
    val popularRestaurants: List<RestaurantWithFavorite> = emptyList(),
    val categories: List<String> = listOf("All"),
    val selectedCategory: String = "All",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val visibleItemsCount: Int = 8,
    val hasMoreItems: Boolean = false
)

class HomeScreenViewModel(
    private val repository: RestaurantRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> get() = _uiState

    private var currentSearchJob: Job? = null
    private val _allRestaurants = MutableStateFlow<List<RestaurantsItem>>(emptyList())

    init {
        observeRestaurants()
    }

    private fun observeRestaurants() {
        viewModelScope.launch {
            // Combine all restaurants with favorite status from database
            combine(
                _allRestaurants,
                repository.getFavoriteRestaurants()
            ) { restaurants, favorites ->
                val favoriteIds = favorites.map { it.id }.toSet()
                restaurants.map { 
                    RestaurantWithFavorite(it, favoriteIds.contains(it.id))
                }
            }.collect { popularWithFavs ->
                val sortedPopular = popularWithFavs.sortedByDescending { r -> r.restaurant.rating }
                _uiState.update { 
                    it.copy(
                        popularRestaurants = sortedPopular,
                        hasMoreItems = sortedPopular.size > it.visibleItemsCount
                    ) 
                }
            }
        }
        getRestaurants()
    }

    fun getRestaurants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, selectedCategory = "All") }
            repository.getRestaurants()
                .catch { e ->
                    val userFriendlyMessage = when (e) {
                        is UnknownHostException -> "No internet connection. Please check your network."
                        is SocketTimeoutException -> "Connection timed out. Please try again later."
                        is IOException -> "Network error. Please check your connection."
                        else -> "An unexpected error occurred."
                    }
                    _uiState.update { it.copy(isLoading = false, errorMessage = userFriendlyMessage) }
                }
                .collect { restaurants ->
                    _allRestaurants.value = restaurants
                    _uiState.update { 
                        it.copy(
                            featuredRestaurants = restaurants.sortedByDescending { r -> r.rating }.take(3),
                            isLoading = false
                        ) 
                    }
                    fetchDynamicCategories(restaurants)
                }
        }
    }

    private fun fetchDynamicCategories(restaurants: List<RestaurantsItem>) {
        viewModelScope.launch {
            val categoriesSet = mutableSetOf<String>()
            val deferreds = restaurants.map { restaurant ->
                async {
                    try {
                        repository.getRestaurantDetail(restaurant.id).firstOrNull()?.let { detail ->
                            detail.categories.forEach { categoriesSet.add(it.name) }
                        }
                    } catch (_: Exception) {
                    }
                }
            }
            deferreds.awaitAll()
            val sortedCategories = listOf("All") + categoriesSet.sorted()
            _uiState.update { it.copy(categories = sortedCategories) }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
        if (category == "All") {
            getRestaurants()
        } else {
            searchRestaurants(category, isCategory = true)
        }
    }

    @OptIn(FlowPreview::class)
    fun searchRestaurants(query: String, isCategory: Boolean = false) {
        if (!isCategory) {
            _uiState.update { it.copy(searchQuery = query, selectedCategory = if (query.isEmpty()) "All" else "") }
        }
        
        currentSearchJob?.cancel()
        
        if (query.isBlank() && !isCategory) {
            getRestaurants()
            return
        }

        currentSearchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (!isCategory) delay(300) 
            
            repository.searchRestaurants(query)
                .catch { e ->
                    val userFriendlyMessage = when (e) {
                        is UnknownHostException -> "No internet connection. Please check your network."
                        is SocketTimeoutException -> "Connection timed out. Please try again later."
                        is IOException -> "Network error. Please check your connection."
                        else -> "An unexpected error occurred."
                    }
                    _uiState.update { it.copy(isLoading = false, errorMessage = userFriendlyMessage) }
                }
                .collect { restaurants ->
                    _allRestaurants.value = restaurants
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun onToggleFavorite(restaurant: RestaurantsItem) {
        viewModelScope.launch {
            val isFav = _uiState.value.popularRestaurants.find { it.restaurant.id == restaurant.id }?.isFavorite ?: false
            if (isFav) {
                repository.deleteFavoriteRestaurant(restaurant.id)
            } else {
                repository.insertFavoriteRestaurant(
                    FavoriteRestaurantEntity(
                        id = restaurant.id,
                        name = restaurant.name,
                        description = restaurant.description,
                        pictureId = restaurant.pictureId,
                        city = restaurant.city,
                        rating = restaurant.rating
                    )
                )
            }
        }
    }

    fun loadMoreItems() {
        _uiState.update { 
            val newCount = it.visibleItemsCount + 8
            it.copy(
                visibleItemsCount = newCount,
                hasMoreItems = it.popularRestaurants.size > newCount
            )
        }
    }

    fun clearQuery() {
        _uiState.update { it.copy(searchQuery = "") }
        getRestaurants()
    }
}

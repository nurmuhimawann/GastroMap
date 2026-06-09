package com.nurmuhimawann.gastromap.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurmuhimawann.gastromap.data.RestaurantRepository
import com.nurmuhimawann.gastromap.data.Result
import com.nurmuhimawann.gastromap.data.local.entity.FavoriteRestaurantEntity
import com.nurmuhimawann.gastromap.data.remote.response.CustomerReviewsItem
import com.nurmuhimawann.gastromap.data.remote.response.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class DetailUiState(
    val result: Result<Restaurant> = Result.Loading,
    val isFavorite: Boolean = false,
    val postReviewResult: Result<Unit>? = null
)

class DetailScreenViewModel(
    private val repository: RestaurantRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> get() = _uiState

    fun getDetailRestaurant(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(result = Result.Loading) }
            repository.getRestaurantDetail(id)
                .catch { e ->
                    val userFriendlyMessage = when (e) {
                        is UnknownHostException -> "No internet connection. Please check your network."
                        is SocketTimeoutException -> "Connection timed out. Please try again later."
                        is IOException -> "Network error. Please check your connection."
                        else -> "An unexpected error occurred."
                    }
                    _uiState.update { it.copy(result = Result.Error(userFriendlyMessage)) }
                }
                .collect { restaurant ->
                   if (restaurant != null) {
                       val sortedRestaurant = restaurant.copy(
                           customerReviews = sortReviews(restaurant.customerReviews)
                       )
                       _uiState.update { it.copy(result = Result.Success(sortedRestaurant)) }
                   }
                }
        }
    }

    fun postReview(id: String, name: String, review: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(postReviewResult = Result.Loading) }
            repository.postReview(id, name, review).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val currentRestaurantResult = _uiState.value.result
                        if (currentRestaurantResult is Result.Success) {
                            val updatedRestaurant = currentRestaurantResult.data.copy(
                                customerReviews = sortReviews(result.data)
                            )
                            _uiState.update {
                                it.copy(
                                    result = Result.Success(updatedRestaurant),
                                    postReviewResult = Result.Success(Unit)
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(postReviewResult = Result.Error(result.errorMessage)) }
                    }
                    Result.Loading -> {
                        _uiState.update { it.copy(postReviewResult = Result.Loading) }
                    }
                }
            }
        }
    }

    fun resetPostReviewResult() {
        _uiState.update { it.copy(postReviewResult = null) }
    }

    fun toggleFavorite(restaurant: FavoriteRestaurantEntity) {
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                repository.deleteFavoriteRestaurant(restaurant.id)
            } else {
                repository.insertFavoriteRestaurant(restaurant)
            }
        }
    }

    fun isFavoriteRestaurant(id: String) {
        viewModelScope.launch {
            repository.isFavoriteRestaurant(id).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    private fun sortReviews(reviews: List<CustomerReviewsItem>): List<CustomerReviewsItem> {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id-ID"))

        // Membalikkan list terlebih dahulu agar ulasan yang paling baru (yang berada di akhir list API)
        // diposisikan di awal sebelum diurutkan secara stabil berdasarkan tanggal.
        return reviews.asReversed().map { review ->
            val date = runCatching { LocalDate.parse(review.date, formatter) }.getOrNull()
            review to date
        }.sortedWith(
            compareByDescending<Pair<CustomerReviewsItem, LocalDate?>> {
                it.second?.year ?: 0
            }.thenByDescending {
                it.second?.monthValue ?: 0
            }.thenByDescending {
                it.second?.dayOfMonth ?: 0
            }
        ).map { it.first }
    }
}

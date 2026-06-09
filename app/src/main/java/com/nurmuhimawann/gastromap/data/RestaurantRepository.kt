package com.nurmuhimawann.gastromap.data

import com.nurmuhimawann.gastromap.data.local.entity.FavoriteRestaurantEntity
import com.nurmuhimawann.gastromap.data.local.room.FavoriteRestaurantDao
import com.nurmuhimawann.gastromap.data.remote.response.CustomerReviewsItem
import com.nurmuhimawann.gastromap.data.remote.response.Restaurant
import com.nurmuhimawann.gastromap.data.remote.response.RestaurantsItem
import com.nurmuhimawann.gastromap.data.remote.response.ReviewRequest
import com.nurmuhimawann.gastromap.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RestaurantRepository(
    private val apiService: ApiService,
    private val favoriteRestaurantDao: FavoriteRestaurantDao
) {

    fun getRestaurants(): Flow<List<RestaurantsItem>> = flow {
        val response = apiService.getRestaurants()
        emit(response.restaurants)
    }

    fun getRestaurantDetail(id: String): Flow<Restaurant?> = flow {
        val response = apiService.getRestaurantDetail(id)
        emit(response.restaurant)
    }

    fun searchRestaurants(query: String): Flow<List<RestaurantsItem>> = flow {
        val response = apiService.searchRestaurants(query)
        emit(response.restaurants)
    }

    fun getFavoriteRestaurants(): Flow<List<FavoriteRestaurantEntity>> {
        return favoriteRestaurantDao.getFavoriteRestaurants()
    }

    fun searchFavoriteRestaurants(query: String): Flow<List<FavoriteRestaurantEntity>> {
        return favoriteRestaurantDao.searchFavoriteRestaurant(query)
    }

    suspend fun insertFavoriteRestaurant(favoriteRestaurantEntity: FavoriteRestaurantEntity) {
        favoriteRestaurantDao.insertFavoriteRestaurant(favoriteRestaurantEntity)
    }

    suspend fun deleteFavoriteRestaurant(id: String) {
        favoriteRestaurantDao.deleteFavoriteRestaurant(id)
    }

    fun isFavoriteRestaurant(id: String): Flow<Boolean> {
        return favoriteRestaurantDao.isFavoriteRestaurant(id)
    }

    fun postReview(id: String, name: String, review: String): Flow<Result<List<CustomerReviewsItem>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.postReview(ReviewRequest(id, name, review))
            if (!response.error) {
                emit(Result.Success(response.customerReviews))
            } else {
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred while posting review"))
        }
    }

    companion object {
        @Volatile
        private var instance: RestaurantRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteRestaurantDao: FavoriteRestaurantDao
        ): RestaurantRepository = instance ?: synchronized(this) {
            instance ?: RestaurantRepository(apiService, favoriteRestaurantDao)
        }.also { instance = it }
    }
}

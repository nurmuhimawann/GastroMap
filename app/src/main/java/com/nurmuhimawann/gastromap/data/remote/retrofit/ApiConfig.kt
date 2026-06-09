package com.nurmuhimawann.gastromap.data.remote.retrofit

import com.nurmuhimawann.gastromap.BuildConfig
import com.nurmuhimawann.gastromap.data.remote.response.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        fun getApiService(): ApiService {
            return try {
                val loggingInterceptor = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }

                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                retrofit.create(ApiService::class.java)
            } catch (e: Throwable) {
                object : ApiService {
                    override suspend fun getRestaurants(): RestaurantListResponse = throw e
                    override suspend fun getRestaurantDetail(id: String): RestaurantDetailResponse = throw e
                    override suspend fun searchRestaurants(query: String): RestaurantSearchResponse = throw e
                    override suspend fun postReview(reviewRequest: ReviewRequest): PostReviewResponse = throw e
                }
            }
        }

        fun getApiGithubService(): ApiGithubService {
            return try {
                val loggingInterceptor = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }

                val client = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL_GITHUB)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()

                retrofit.create(ApiGithubService::class.java)
            } catch (e: Throwable) {
                object : ApiGithubService {
                    override suspend fun getDetailGithubUser(username: String): GithubDetailUser = throw e
                }
            }
        }
    }
}

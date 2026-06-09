package com.nurmuhimawann.gastromap.data.remote.retrofit

import com.nurmuhimawann.gastromap.data.remote.response.GithubDetailUser
import com.nurmuhimawann.gastromap.data.remote.response.PostReviewResponse
import com.nurmuhimawann.gastromap.data.remote.response.RestaurantDetailResponse
import com.nurmuhimawann.gastromap.data.remote.response.RestaurantListResponse
import com.nurmuhimawann.gastromap.data.remote.response.RestaurantSearchResponse
import com.nurmuhimawann.gastromap.data.remote.response.ReviewRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("list")
    suspend fun getRestaurants(): RestaurantListResponse

    @GET("detail/{id}")
    suspend fun getRestaurantDetail(
        @Path("id") id: String
    ): RestaurantDetailResponse

    @GET("search")
    suspend fun searchRestaurants(
        @Query("q") query: String
    ): RestaurantSearchResponse

    @POST("review")
    suspend fun postReview(
        @Body reviewRequest: ReviewRequest
    ): PostReviewResponse

}

interface ApiGithubService {
    @GET("users/{username}")
    suspend fun getDetailGithubUser(
        @Path("username") username: String
    ): GithubDetailUser
}

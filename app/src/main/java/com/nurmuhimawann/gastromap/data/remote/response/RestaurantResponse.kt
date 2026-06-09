package com.nurmuhimawann.gastromap.data.remote.response

import com.google.gson.annotations.SerializedName

data class RestaurantListResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("count")
    val count: Int,
    @field:SerializedName("restaurants")
    val restaurants: List<RestaurantsItem>
)

data class RestaurantDetailResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("restaurant")
    val restaurant: Restaurant
)

data class RestaurantSearchResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("founded")
    val founded: Int,
    @field:SerializedName("restaurants")
    val restaurants: List<RestaurantsItem>
)

data class RestaurantsItem(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("pictureId")
    val pictureId: String,
    @field:SerializedName("city")
    val city: String,
    @field:SerializedName("rating")
    val rating: Double
)

data class Restaurant(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("pictureId")
    val pictureId: String,
    @field:SerializedName("city")
    val city: String,
    @field:SerializedName("rating")
    val rating: Double,
    @field:SerializedName("address")
    val address: String,
    @field:SerializedName("categories")
    val categories: List<CategoriesItem>,
    @field:SerializedName("menus")
    val menus: Menus,
    @field:SerializedName("customerReviews")
    val customerReviews: List<CustomerReviewsItem>
)

data class CategoriesItem(
    @field:SerializedName("name")
    val name: String
)

data class Menus(
    @field:SerializedName("foods")
    val foods: List<FoodsItem>,
    @field:SerializedName("drinks")
    val drinks: List<DrinksItem>
)

data class FoodsItem(
    @field:SerializedName("name")
    val name: String
)

data class DrinksItem(
    @field:SerializedName("name")
    val name: String
)

data class CustomerReviewsItem(
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("review")
    val review: String,
    @field:SerializedName("date")
    val date: String
)

data class PostReviewResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("customerReviews")
    val customerReviews: List<CustomerReviewsItem>
)

data class ReviewRequest(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("review")
    val review: String
)

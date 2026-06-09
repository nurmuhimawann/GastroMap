package com.nurmuhimawann.gastromap.common

object Helpers {
    fun smallRestaurantImage(pictureId: String): String {
        return "https://restaurant-api.dicoding.dev/images/small/$pictureId"
    }

    fun mediumRestaurantImage(pictureId: String): String {
        return "https://restaurant-api.dicoding.dev/images/medium/$pictureId"
    }

    fun largeRestaurantImage(pictureId: String): String {
        return "https://restaurant-api.dicoding.dev/images/large/$pictureId"
    }

    fun avatarGenerator(username: String): String {
        return "https://ui-avatars.com/api/?name=$username&length=1&background=random&size=128"
    }
}

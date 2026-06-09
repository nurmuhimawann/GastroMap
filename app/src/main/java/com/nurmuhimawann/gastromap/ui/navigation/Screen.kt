package com.nurmuhimawann.gastromap.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorite : Screen("favorite")

    data object About : Screen("about")

    data object DetailRestaurant : Screen("home/{id}") {
        fun createRoute(id: String) = "home/$id"
    }
}

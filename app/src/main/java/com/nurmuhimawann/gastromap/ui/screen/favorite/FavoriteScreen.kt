package com.nurmuhimawann.gastromap.ui.screen.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nurmuhimawann.gastromap.data.Result
import com.nurmuhimawann.gastromap.data.remote.response.RestaurantsItem
import com.nurmuhimawann.gastromap.di.Injection
import com.nurmuhimawann.gastromap.ui.components.ErrorText
import com.nurmuhimawann.gastromap.ui.components.PopularRestaurantCard
import com.nurmuhimawann.gastromap.ui.components.RestaurantCardLoader
import com.nurmuhimawann.gastromap.ui.components.FavoriteTopBar
import com.nurmuhimawann.gastromap.ui.navigation.Screen

@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewModel: FavoriteScreenViewModel = viewModel(
        factory = Injection.provideViewModelFactory(
            LocalContext.current
        )
    ),
    isInDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoriteScreenContent(
        uiState = uiState,
        navigateToDetail = { id ->
            navController.navigate(
                Screen.DetailRestaurant.createRoute(id)
            )
        },
        isInDarkMode = isInDarkMode,
        onToggleDarkMode = onToggleDarkMode,
        onToggleFavorite = { restaurant ->
            viewModel.onToggleFavorite(restaurant)
        },
        onQueryChange = { query ->
            viewModel.searchFavoriteRestaurants(query)
        },
        onClearQuery = {
            viewModel.clearQuery()
        }
    )
}

@Composable
fun FavoriteScreenContent(
    uiState: FavoriteUiState,
    navigateToDetail: (String) -> Unit,
    isInDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleFavorite: (RestaurantsItem) -> Unit,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            FavoriteTopBar(
                onToggleDarkMode = onToggleDarkMode,
                isInDarkMode = isInDarkMode,
                query = uiState.searchQuery,
                onQueryChange = onQueryChange,
                onClearQuery = onClearQuery
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (val result = uiState.result) {
                is Result.Loading -> {
                    LazyColumn {
                        items(10) { RestaurantCardLoader() }
                    }
                }

                is Result.Success -> {
                    val restaurant = result.data
                    if (restaurant.isEmpty()) {
                        Column(
                            modifier = modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (uiState.searchQuery.isEmpty()) "No favorite restaurant at the moment" else "No results found for \"${uiState.searchQuery}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = modifier
                        ) {
                            items(restaurant, key = { it.id }) { item ->
                            val restaurantItem = RestaurantsItem(
                                id = item.id,
                                name = item.name,
                                description = item.description,
                                pictureId = item.pictureId,
                                city = item.city,
                                rating = item.rating
                            )
                            PopularRestaurantCard(
                                restaurant = restaurantItem,
                                isFavorite = true,
                                onToggleFavorite = { onToggleFavorite(restaurantItem) },
                                onClick = { navigateToDetail(item.id) }
                            )
                        }
                    }
                }
            }

                is Result.Error -> {
                    ErrorText(
                        result.errorMessage
                    )
                }
            }
        }
    }
}

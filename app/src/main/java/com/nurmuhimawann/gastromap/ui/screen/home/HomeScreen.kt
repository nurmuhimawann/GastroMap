package com.nurmuhimawann.gastromap.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nurmuhimawann.gastromap.di.Injection
import com.nurmuhimawann.gastromap.ui.components.CategoryChip
import com.nurmuhimawann.gastromap.ui.components.ErrorText
import com.nurmuhimawann.gastromap.ui.components.FeaturedRestaurantCard
import com.nurmuhimawann.gastromap.ui.components.HomeTopBar
import com.nurmuhimawann.gastromap.ui.components.PopularRestaurantCard
import com.nurmuhimawann.gastromap.ui.components.RestaurantCardLoader
import com.nurmuhimawann.gastromap.ui.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeScreenViewModel = viewModel(
        factory = Injection.provideViewModelFactory(
            LocalContext.current
        )
    ),
    isInDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        navigateToDetail = { id ->
            navController.navigate(
                Screen.DetailRestaurant.createRoute(id)
            )
        },
        isInDarkMode = isInDarkMode,
        onToggleDarkMode = onToggleDarkMode,
        onQueryChange = { query ->
            viewModel.searchRestaurants(query)
        },
        onClearQuery = {
            viewModel.clearQuery()
        },
        onCategorySelect = { category ->
            viewModel.selectCategory(category)
        },
        onToggleFavorite = { restaurant ->
            viewModel.onToggleFavorite(restaurant.restaurant)
        },
        onLoadMore = {
            viewModel.loadMoreItems()
        }
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    navigateToDetail: (String) -> Unit,
    isInDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onToggleFavorite: (RestaurantWithFavorite) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                query = uiState.searchQuery,
                onQueryChange = onQueryChange,
                isInDarkMode = isInDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onClearQuery = onClearQuery
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isLoading && uiState.featuredRestaurants.isEmpty()) {
                LazyColumn {
                    items(10) { RestaurantCardLoader() }
                }
            } else if (uiState.errorMessage != null) {
                ErrorText(
                    uiState.errorMessage
                )
            } else {
                val isSearching = uiState.searchQuery.isNotEmpty()
                
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                if (!isSearching) {
                    // Featured Restaurants Section
                    if (uiState.featuredRestaurants.isNotEmpty()) {
                        item {
                            Text(
                                text = "Featured Restaurants",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                items(uiState.featuredRestaurants, key = { it.id }) { item ->
                                    FeaturedRestaurantCard(
                                        restaurant = item,
                                        onClick = { navigateToDetail(item.id) }
                                    )
                                }
                            }
                        }
                    }

                    // Categories Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Browse by",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(uiState.categories) { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = uiState.selectedCategory == category,
                                    onClick = { onCategorySelect(category) }
                                )
                            }
                        }
                    }

                    // Popular Restaurants Section Title
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Popular Restaurants",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    // Search Result Title
                    item {
                        Text(
                            text = "Search results for \"${uiState.searchQuery}\"",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                if (uiState.popularRestaurants.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp)
                        ) {
                            Text(
                                text = "No restaurants found",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    val visibleItems = uiState.popularRestaurants.take(uiState.visibleItemsCount)
                    items(visibleItems, key = { it.restaurant.id }) { item ->
                        PopularRestaurantCard(
                            restaurant = item.restaurant,
                            isFavorite = item.isFavorite,
                            onToggleFavorite = { onToggleFavorite(item) },
                            onClick = { navigateToDetail(item.restaurant.id) }
                        )
                    }

                    if (uiState.hasMoreItems) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = onLoadMore,
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                ) {
                                    Text(
                                        text = "Load More",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

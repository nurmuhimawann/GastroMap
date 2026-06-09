package com.nurmuhimawann.gastromap.ui.screen.detail

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.nurmuhimawann.gastromap.common.Helpers
import com.nurmuhimawann.gastromap.data.Result
import com.nurmuhimawann.gastromap.data.local.entity.FavoriteRestaurantEntity
import com.nurmuhimawann.gastromap.data.remote.response.CategoriesItem
import com.nurmuhimawann.gastromap.data.remote.response.CustomerReviewsItem
import com.nurmuhimawann.gastromap.data.remote.response.DrinksItem
import com.nurmuhimawann.gastromap.data.remote.response.FoodsItem
import com.nurmuhimawann.gastromap.data.remote.response.Menus
import com.nurmuhimawann.gastromap.data.remote.response.Restaurant
import com.nurmuhimawann.gastromap.di.Injection
import com.nurmuhimawann.gastromap.ui.components.DetailRestaurantLoader
import com.nurmuhimawann.gastromap.ui.components.ErrorText
import com.nurmuhimawann.gastromap.ui.components.MenuItem
import com.nurmuhimawann.gastromap.ui.components.MenuType
import com.nurmuhimawann.gastromap.ui.components.RestaurantCategories
import com.nurmuhimawann.gastromap.ui.components.ReviewSection
import com.nurmuhimawann.gastromap.ui.components.DetailTopBar
import com.nurmuhimawann.gastromap.ui.components.shimmerBrush
import com.nurmuhimawann.gastromap.ui.theme.GastroMapTheme

@Composable
fun DetailScreen(
    restaurantId: String, viewModel: DetailScreenViewModel = viewModel(
        factory = Injection.provideViewModelFactory(
            LocalContext.current
        )
    ), navigateBack: () -> Unit, isInDarkMode: Boolean, onToggleDarkMode: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(restaurantId) {
        viewModel.getDetailRestaurant(restaurantId)
        viewModel.isFavoriteRestaurant(restaurantId)
    }

    LaunchedEffect(uiState.postReviewResult) {
        uiState.postReviewResult?.let { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(context, "Review added successfully", Toast.LENGTH_SHORT).show()
                    showReviewDialog = false
                    viewModel.resetPostReviewResult()
                }
                is Result.Error -> {
                    Toast.makeText(context, "Error: ${result.errorMessage}", Toast.LENGTH_SHORT).show()
                    viewModel.resetPostReviewResult()
                }
                Result.Loading -> {}
            }
        }
    }

    if (showReviewDialog) {
        AddReviewDialog(
            onDismiss = { showReviewDialog = false },
            onConfirm = { name, review ->
                viewModel.postReview(restaurantId, name, review)
            },
            isLoading = uiState.postReviewResult is Result.Loading
        )
    }

    when (val result = uiState.result) {
        is Result.Loading -> {
            DetailRestaurantLoader()
        }

        is Result.Success -> {
            DetailScreenContent(
                restaurantDetail = result.data,
                navigateBack = navigateBack,
                isInDarkMode = isInDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onToggleFavorite = viewModel::toggleFavorite,
                isFavorite = uiState.isFavorite,
                onAddReviewClick = { showReviewDialog = true }
            )
        }

        is Result.Error -> {
            ErrorText(
                result.errorMessage
            )
        }
    }
}

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf("") }
    var review by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Review") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text("Review") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, review) },
                enabled = name.isNotBlank() && review.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Post")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    restaurantDetail: Restaurant,
    navigateBack: () -> Unit,
    isInDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleFavorite: (FavoriteRestaurantEntity) -> Unit,
    isFavorite: Boolean,
    onAddReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = {
        DetailTopBar(
            title = restaurantDetail.name,
            onToggleDarkMode = onToggleDarkMode,
            isInDarkMode = isInDarkMode,
            onBackClick = navigateBack
        )
    }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                SubcomposeAsyncImage(
                    model = Helpers.mediumRestaurantImage(restaurantDetail.pictureId),
                    contentDescription = restaurantDetail.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(shimmerBrush())
                        )
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
                
                IconButton(
                    onClick = {
                        onToggleFavorite(
                            FavoriteRestaurantEntity(
                                id = restaurantDetail.id,
                                name = restaurantDetail.name,
                                description = restaurantDetail.description,
                                pictureId = restaurantDetail.pictureId,
                                city = restaurantDetail.city,
                                rating = restaurantDetail.rating,
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = restaurantDetail.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color(0xFFFFD600),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = restaurantDetail.rating.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = restaurantDetail.city,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(24.dp))

                // Address Section
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = "Address",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = restaurantDetail.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description Section
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = restaurantDetail.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                LazyRow(
                    modifier = Modifier.padding(start = 0.dp)
                ) {
                    items(restaurantDetail.categories, key = { it.name }) { category ->
                        Row {
                            RestaurantCategories(
                                category.name
                            )
                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Menus",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(restaurantDetail.menus.foods, key = { it.name }) { menu ->
                        MenuItem(
                            name = menu.name,
                            type = MenuType.FOOD
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Drinks",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(
                    modifier = Modifier.height(12.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(restaurantDetail.menus.drinks, key = { it.name }) { menu ->
                        MenuItem(
                            name = menu.name,
                            type = MenuType.DRINK
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                ReviewSection(
                    reviews = restaurantDetail.customerReviews,
                    onAddReviewClick = onAddReviewClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenContentPreview() {
    GastroMapTheme {
        DetailScreenContent(
            restaurantDetail = Restaurant(
                id = "restaurantId",
                name = "Restaurant Name",
                description = "Restaurant Description",
                pictureId = "restaurantPictureId",
                city = "Restaurant City",
                rating = 4.5,
                address = "Restaurant Address",
                categories = listOf(
                    CategoriesItem(
                        name = "Category Name"
                    )
                ),
                customerReviews = listOf(
                    CustomerReviewsItem(
                        name = "Customer Name", review = "Customer Review", date = "Customer Date"
                    )
                ),
                menus = Menus(
                    foods = listOf(
                        FoodsItem(
                            name = "Food Name",
                        )
                    ), drinks = listOf(
                        DrinksItem(
                            name = "Drink Name",
                        )
                    )
                )
            ),
            navigateBack = {},
            isInDarkMode = false,
            onToggleDarkMode = {},
            onToggleFavorite = {},
            isFavorite = true,
            onAddReviewClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DetailScreenContentDarkPreview() {
    GastroMapTheme {
        DetailScreenContent(
            restaurantDetail = Restaurant(
                id = "restaurantId",
                name = "Restaurant Name",
                description = "Restaurant Description",
                pictureId = "restaurantPictureId",
                city = "Restaurant City",
                rating = 4.5,
                address = "Restaurant Address",
                categories = listOf(
                    CategoriesItem(
                        name = "Category Name"
                    )
                ),
                customerReviews = listOf(
                    CustomerReviewsItem(
                        name = "Customer Name", review = "Customer Review", date = "Customer Date"
                    )
                ),
                menus = Menus(
                    foods = listOf(
                        FoodsItem(
                            name = "Food Name",
                        )
                    ), drinks = listOf(
                        DrinksItem(
                            name = "Drink Name",
                        )
                    )
                )
            ),
            navigateBack = {},
            isInDarkMode = true,
            onToggleDarkMode = {},
            onToggleFavorite = {},
            isFavorite = false,
            onAddReviewClick = {}
        )
    }
}

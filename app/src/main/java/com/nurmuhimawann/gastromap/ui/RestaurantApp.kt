package com.nurmuhimawann.gastromap.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nurmuhimawann.gastromap.di.Injection
import com.nurmuhimawann.gastromap.ui.navigation.NavigationItem
import com.nurmuhimawann.gastromap.ui.navigation.Screen
import com.nurmuhimawann.gastromap.ui.screen.about.AboutScreen
import com.nurmuhimawann.gastromap.ui.screen.detail.DetailScreen
import com.nurmuhimawann.gastromap.ui.screen.favorite.FavoriteScreen
import com.nurmuhimawann.gastromap.ui.screen.home.HomeScreen
import com.nurmuhimawann.gastromap.ui.theme.GastroMapTheme

@Composable
fun GastroMap(
    navController: NavHostController = rememberNavController(),
    viewModel: GastroMapViewModel = viewModel(
        factory = Injection.provideViewModelFactory(
            LocalContext.current
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    GastroMapTheme(
        darkTheme = uiState.isDarkMode
    ) {
        Scaffold(
            bottomBar = {
                BottomBar(navController = navController, currentRoute = currentRoute)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(
                    Screen.Home.route
                ) {
                    HomeScreen(
                        navController = navController,
                        onToggleDarkMode = {
                            viewModel.setDarkMode(!uiState.isDarkMode)
                        },
                        isInDarkMode = uiState.isDarkMode,
                    )
                }
                composable(
                    Screen.Favorite.route
                ) {
                    FavoriteScreen(
                        navController = navController,
                        onToggleDarkMode = {
                            viewModel.setDarkMode(!uiState.isDarkMode)
                        },
                        isInDarkMode = uiState.isDarkMode,
                    )
                }
                composable(Screen.About.route) {
                    AboutScreen(
                        isInDarkMode = uiState.isDarkMode,
                        onToggleDarkMode = {
                            viewModel.setDarkMode(!uiState.isDarkMode)
                        }
                    )
                }
                composable(
                    route = Screen.DetailRestaurant.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    val id = it.arguments?.getString("id") ?: ""
                    DetailScreen(restaurantId = id, navigateBack = {
                        navController.navigateUp()
                    }, isInDarkMode = uiState.isDarkMode, onToggleDarkMode = {
                        viewModel.setDarkMode(!uiState.isDarkMode)
                    })
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        val navigationItems = listOf(
            NavigationItem(
                title = "Home",
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            NavigationItem(
                title = "Favorite",
                icon = Icons.Default.Bookmark,
                screen = Screen.Favorite
            ),
            NavigationItem(
                title = "About",
                icon = Icons.Default.Person,
                screen = Screen.About
            ),
        )
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GastroMapTheme {
        GastroMap()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkPreview() {
    GastroMapTheme {
        GastroMap()
    }
}

// kotlin
package com.example.jetpackcomposeapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.jetpackcomposeapp.ui.screens.*
import com.example.jetpackcomposeapp.viewmodel.CatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(navController: NavHostController, catViewModel: CatViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val canNavigateBack = navController.previousBackStackEntry != null
    val title = when {
        currentRoute == NavigationItem.Home.route -> "O Autorze"
        currentRoute == NavigationItem.Cats.route -> "Moje Koty"
        currentRoute == NavigationItem.Gallery.route -> "Galeria Zdjęć"
        currentRoute == NavigationItem.Add.route -> "Dodaj Kota"
        currentRoute == NavigationItem.Camera.route -> "Aparat"
        currentRoute?.startsWith("detail") == true -> "Szczegóły Kota"
        currentRoute?.startsWith("edit") == true -> "Edycja Danych"
        else -> "Jetpack Compose App"
    }

    Scaffold(
        topBar = {
            if (currentRoute?.startsWith("viewer") != true &&
                currentRoute?.startsWith("galleryViewer") != true &&
                currentRoute?.startsWith("camera") != true) {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (currentRoute == NavigationItem.Home.route ||
                currentRoute == NavigationItem.Cats.route ||
                currentRoute == NavigationItem.Gallery.route) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Home.route) { HomeScreen(navController, catViewModel) }
            composable(NavigationItem.Cats.route) { CatListScreen(navController, catViewModel) }
            composable(NavigationItem.Gallery.route) { GalleryGridScreen(navController, catViewModel) }
            composable(NavigationItem.Add.route) { CatAddScreen(navController, catViewModel) }
            composable(NavigationItem.Camera.route) { CameraScreen(navController, catViewModel) }

            composable(
                route = NavigationItem.CameraWithCallback.route,
                arguments = listOf(navArgument("callbackMode") { type = NavType.StringType })
            ) { backStackEntry ->
                val callbackKey = backStackEntry.arguments?.getString("callbackMode")
                CameraScreen(navController, catViewModel, callbackKey)
            }

            composable(
                route = NavigationItem.Detail.route,
                arguments = listOf(navArgument("catId") { type = NavType.IntType })
            ) { backStackEntry ->
                val catId = backStackEntry.arguments?.getInt("catId") ?: 1
                CatDetailScreen(navController, catId, catViewModel)
            }

            composable(
                route = NavigationItem.Edit.route,
                arguments = listOf(navArgument("catId") { type = NavType.IntType })
            ) { backStackEntry ->
                val catId = backStackEntry.arguments?.getInt("catId") ?: 1
                CatEditScreen(navController, catId, catViewModel)
            }

            composable(
                route = NavigationItem.Viewer.route,
                arguments = listOf(
                    navArgument("catId") { type = NavType.IntType },
                    navArgument("imageIndex") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val catId = backStackEntry.arguments?.getInt("catId") ?: 0
                val imageIndex = backStackEntry.arguments?.getInt("imageIndex") ?: 0
                ImageViewerScreen(navController, catId, imageIndex, catViewModel)
            }

            composable(
                route = NavigationItem.GalleryViewer.route,
                arguments = listOf(navArgument("imageIndex") { type = NavType.IntType })
            ) { backStackEntry ->
                val imageIndex = backStackEntry.arguments?.getInt("imageIndex") ?: 0
                GalleryViewerScreen(navController, imageIndex, catViewModel)
            }

            // Trasa dla wyboru obrazu z galerii aplikacji (z callbackKey)
            composable(
                route = NavigationItem.GallerySelect.route,
                arguments = listOf(navArgument("callbackKey") { type = NavType.StringType })
            ) { backStackEntry ->
                val callbackKey = backStackEntry.arguments?.getString("callbackKey")
                GalleryGridScreen(navController, catViewModel, callbackKey)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(NavigationItem.Home, NavigationItem.Cats, NavigationItem.Gallery)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

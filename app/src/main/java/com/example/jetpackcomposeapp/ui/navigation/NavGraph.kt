package com.example.jetpackcomposeapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeapp.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Cats,
        NavigationItem.Gallery
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Jetpack Compose App") })
        },
        bottomBar = {
            BottomNavigationBar(navController, items)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Home.route) { HomeScreen(navController) }
            composable(NavigationItem.Cats.route) { CatListScreen(navController) }
            composable(NavigationItem.Gallery.route) { ImageViewerScreen(navController) }
            composable(NavigationItem.Detail.route) { backStackEntry ->
                val catId = backStackEntry.arguments?.getInt("catId")
                CatDetailScreen(navController, catId ?: 1)
            }
            composable(NavigationItem.Edit.route) { CatEditScreen(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<NavigationItem>) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

package com.example.jetpackcomposeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavigationItem("home", "Home", Icons.Default.Home)
    object Cats : NavigationItem("list", "Cats", Icons.Default.Lock)
    object Gallery : NavigationItem("gallery", "Gallery", Icons.Default.ShoppingCart)
    object Detail : NavigationItem("detail/{catId}", "Detail", Icons.Default.Favorite)
    object Edit : NavigationItem("edit/{catId}", "Edit", Icons.Default.Favorite)
    object Add : NavigationItem("add", "Add Cat", Icons.Default.Call)
    object Camera : NavigationItem("camera", "Camera", Icons.Default.Call)
    object CameraWithCallback : NavigationItem("camera/{callbackMode}", "Camera", Icons.Default.Call)
    object Viewer : NavigationItem("viewer/{catId}/{imageIndex}", "Viewer", Icons.Default.ShoppingCart)
    object GalleryViewer : NavigationItem("galleryViewer/{imageIndex}", "Gallery Viewer", Icons.Default.ShoppingCart)
}

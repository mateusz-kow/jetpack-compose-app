package com.example.jetpackcomposeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavigationItem("home", "Dom", Icons.Default.Home)
    object Cats : NavigationItem("list", "Koty", Icons.Default.Pets)
    object Gallery : NavigationItem("gallery", "Galeria", Icons.Default.Photo)
    object Detail : NavigationItem("detail/{catId}", "Szczegóły", Icons.Default.Info)
    object Edit : NavigationItem("edit/{catId}", "Edycja", Icons.Default.Edit)
    object Add : NavigationItem("add", "Dodaj kota", Icons.Default.Add)
    object Camera : NavigationItem("camera", "Kamera", Icons.Default.CameraAlt)
    object CameraWithCallback : NavigationItem("camera/{callbackMode}", "Kamera", Icons.Default.Cameraswitch)
    object Viewer : NavigationItem("viewer/{catId}/{imageIndex}", "Viewer", Icons.Default.Photo)
    object GalleryViewer : NavigationItem("galleryViewer/{imageIndex}", "Gallery Viewer", Icons.Default.Photo)
    object GallerySelect : NavigationItem("gallerySelect/{callbackKey}", "Gallery Select", Icons.Default.Photo)
}

package com.example.jetpackcomposeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : NavigationItem("home", "Dom", Icons.Default.Home)
    object Cats : NavigationItem("list", "Koty", Icons.Default.Pets)
    object Gallery : NavigationItem("gallery", "Galeria", Icons.Default.Photo)
    object Settings : NavigationItem("settings", "Ustawienia", Icons.Default.Settings)
    object Detail : NavigationItem("detail/{catId}", "Szczegóły", Icons.Default.Info)
    object Edit : NavigationItem("edit/{catId}", "Edycja", Icons.Default.Edit)
    object Add : NavigationItem("add", "Dodaj kota", Icons.Default.Add)
    object Camera : NavigationItem("camera", "Kamera", Icons.Default.CameraAlt)
    object CameraWithCallback : NavigationItem("camera/{callbackMode}", "Kamera", Icons.Default.CameraAlt)
    object Viewer : NavigationItem("viewer/{catId}/{imageIndex}", "Podgląd", Icons.Default.Image)
    object GalleryViewer : NavigationItem("galleryViewer/{imageIndex}", "Podgląd galerii", Icons.Default.Collections)
    object GallerySelect : NavigationItem("gallerySelect/{callbackKey}", "Wybór zdjęcia", Icons.Default.PhotoLibrary)
}



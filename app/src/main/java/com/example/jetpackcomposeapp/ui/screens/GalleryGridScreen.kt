package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.ui.utils.hasCameraPermissions
import com.example.jetpackcomposeapp.ui.utils.rememberMultiplePermissionsLauncher
import com.example.jetpackcomposeapp.ui.utils.getCameraPermissions
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import java.io.File

@Composable
fun GalleryGridScreen(navController: NavHostController, viewModel: CatViewModel) {
    val allImages by viewModel.allImages.collectAsState()
    val context = LocalContext.current

    // Camera permission launcher
    val cameraPermissionLauncher = rememberMultiplePermissionsLauncher { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            navController.navigate("camera")
        }
    }

    fun requestCameraAccess() {
        if (hasCameraPermissions(context)) {
            navController.navigate("camera")
        } else {
            cameraPermissionLauncher.launch(getCameraPermissions())
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { requestCameraAccess() }
            ) {
                Icon(Icons.Default.ThumbUp, contentDescription = "Zrób zdjęcie")
            }
        }
    ) { paddingValues ->
        if (allImages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Brak zdjęć w galerii!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "Zrób pierwsze zdjęcie używając przycisku aparatu",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 1.dp,
                    top = paddingValues.calculateTopPadding() + 1.dp,
                    end = 1.dp,
                    bottom = paddingValues.calculateBottomPadding() + 1.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(allImages) { image ->
                    val imageFile = File(image.localPath)
                    AsyncImage(
                        model = imageFile,
                        contentDescription = "Zdjęcie z galerii",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                // Znajdź indeks tego zdjęcia w liście wszystkich zdjęć
                                val imageIndex = allImages.indexOf(image)
                                navController.navigate("galleryViewer/$imageIndex")
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
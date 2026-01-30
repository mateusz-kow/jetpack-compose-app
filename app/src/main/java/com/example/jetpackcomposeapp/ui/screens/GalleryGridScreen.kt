package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.ui.utils.hasCameraPermissions
import com.example.jetpackcomposeapp.ui.utils.rememberMultiplePermissionsLauncher
import com.example.jetpackcomposeapp.ui.utils.getCameraPermissions
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import kotlinx.coroutines.launch
import java.io.File
import com.example.jetpackcomposeapp.data.model.Image as AppImage

@Composable
fun GalleryGridScreen(navController: NavHostController, viewModel: CatViewModel, callbackKey: String? = null) {
    val allImages by viewModel.allImages.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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

    var imageToDelete by remember { mutableStateOf<AppImage?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { requestCameraAccess() }
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Zrób zdjęcie")
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
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        imageToDelete = image
                                        showDeleteDialog = true
                                    },
                                    onTap = {
                                        if (callbackKey != null) {
                                            // invoke callback with image id and navigate back
                                            viewModel.getCameraCallback(callbackKey)?.invoke(image.id)
                                            viewModel.clearCameraCallback(callbackKey)
                                            navController.popBackStack()
                                        } else {
                                            val imageIndex = allImages.indexOf(image)
                                            navController.navigate("galleryViewer/$imageIndex")
                                        }
                                    }
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        if (showDeleteDialog && imageToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false; imageToDelete = null },
                title = { Text("Usuń zdjęcie") },
                text = { Text("Czy na pewno chcesz usunąć to zdjęcie? Ta akcja usunie plik i dane z aplikacji.") },
                confirmButton = {
                    Button(onClick = {
                        val toDelete = imageToDelete
                        if (toDelete != null) {
                            coroutineScope.launch {
                                viewModel.getImageRepository().deleteImage(toDelete)

                                showDeleteDialog = false
                                imageToDelete = null
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("Usuń", color = MaterialTheme.colorScheme.onError)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false; imageToDelete = null }) { Text("Anuluj") }
                }
            )
        }
    }
}
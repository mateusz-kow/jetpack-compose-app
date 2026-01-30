package com.example.jetpackcomposeapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.data.model.Cat
import com.example.jetpackcomposeapp.data.model.Image
import com.example.jetpackcomposeapp.ui.components.ImagePickerSection
import com.example.jetpackcomposeapp.ui.utils.rememberGalleryLauncher
import com.example.jetpackcomposeapp.ui.utils.rememberMultiplePermissionsLauncher
import com.example.jetpackcomposeapp.ui.utils.getStoragePermissions
import com.example.jetpackcomposeapp.ui.utils.hasStoragePermissions
import com.example.jetpackcomposeapp.ui.utils.getCameraPermissions
import com.example.jetpackcomposeapp.ui.utils.hasCameraPermissions
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import kotlinx.coroutines.launch

@Composable
fun CatEditScreen(navController: NavHostController, catId: Int, viewModel: CatViewModel) {
    var cat by remember { mutableStateOf<Cat?>(null) }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var catImages by remember { mutableStateOf(listOf<Image>()) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Launcher for gallery picker (device gallery)
    val galleryLauncher = rememberGalleryLauncher { uri: Uri ->
        coroutineScope.launch {
            val imageRepository = viewModel.getImageRepository()
            val savedImage = imageRepository.saveImageFromGallery(uri)
            savedImage?.let { image ->
                catImages = catImages + image
            }
        }
    }

    // Permission launchers
    val galleryPermissionLauncher = rememberMultiplePermissionsLauncher { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            galleryLauncher.launch("image/*")
        }
    }

    val cameraPermissionLauncher = rememberMultiplePermissionsLauncher { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            val callbackKey = "catEdit_${catId}_camera_${System.currentTimeMillis()}"
            viewModel.setCameraCallback(callbackKey) { imageId ->
                coroutineScope.launch {
                    val imageRepository = viewModel.getImageRepository()
                    val image = imageRepository.getImageById(imageId)
                    image?.let {
                        catImages = catImages + it
                    }
                }
            }
            navController.navigate("camera/$callbackKey")
        }
    }

    fun requestGalleryAccess() {
        if (hasStoragePermissions(context)) {
            galleryLauncher.launch("image/*")
        } else {
            galleryPermissionLauncher.launch(getStoragePermissions())
        }
    }

    fun requestCameraAccess() {
        if (hasCameraPermissions(context)) {
            val callbackKey = "catEdit_${catId}_camera_${System.currentTimeMillis()}"
            viewModel.setCameraCallback(callbackKey) { imageId ->
                coroutineScope.launch {
                    val imageRepository = viewModel.getImageRepository()
                    val image = imageRepository.getImageById(imageId)
                    image?.let {
                        catImages = catImages + it
                    }
                }
            }
            navController.navigate("camera/$callbackKey")
        } else {
            cameraPermissionLauncher.launch(getCameraPermissions())
        }
    }

    fun requestAppGalleryAccess() {
        val key = "catEdit_${catId}_gallery_${System.currentTimeMillis()}"
        viewModel.setCameraCallback(key) { imageId ->
            coroutineScope.launch {
                val image = viewModel.getImageRepository().getImageById(imageId)
                image?.let { catImages = catImages + it }
            }
        }
        navController.navigate("gallerySelect/$key")
    }

    LaunchedEffect(catId) {
        val loadedCat = viewModel.getCatById(catId)
        cat = loadedCat
        loadedCat?.let {
            name = it.name
            breed = it.breed
            description = it.description
            catImages = viewModel.getImagesForCat(it)
        }
    }

    cat?.let { catData ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Edytuj kota",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pola tekstowe
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Imię kota") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Rasa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sekcja wyboru zdjęć
            ImagePickerSection(
                images = catImages,
                onGalleryClick = { requestGalleryAccess() },
                onCameraClick = { requestCameraAccess() },
                onAppGalleryClick = { requestAppGalleryAccess() },
                onImageRemove = { index ->
                    val newList = catImages.toMutableList()
                    newList.removeAt(index)
                    catImages = newList
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Przyciski akcji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Anuluj")
                }

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.updateCat(
                                catData.copy(
                                    name = name,
                                    breed = breed,
                                    description = description,
                                    imageIds = catImages.map { it.id }
                                )
                            )
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank()
                ) {
                    Text("Zapisz zmiany")
                }
            }
        }
    } ?: run {
        // Loading state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

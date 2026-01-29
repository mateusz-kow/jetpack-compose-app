package com.example.jetpackcomposeapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.data.model.Image as CatImage
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
fun CatAddScreen(navController: NavHostController, viewModel: CatViewModel) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf(listOf<CatImage>()) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Launcher for gallery picker (device gallery)
    val galleryLauncher = rememberGalleryLauncher { uri: Uri ->
        coroutineScope.launch {
            viewModel.getImageRepository().saveImageFromGallery(uri)?.let {
                selectedImages = selectedImages + it
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
            val callbackKey = "add_cat_camera_${System.currentTimeMillis()}"
            viewModel.setCameraCallback(callbackKey) { imageId ->
                coroutineScope.launch {
                    viewModel.getImageRepository().getImageById(imageId)?.let {
                        selectedImages = selectedImages + it
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
            val callbackKey = "add_cat_camera_${System.currentTimeMillis()}"
            viewModel.setCameraCallback(callbackKey) { imageId ->
                coroutineScope.launch {
                    viewModel.getImageRepository().getImageById(imageId)?.let {
                        selectedImages = selectedImages + it
                    }
                }
            }
            navController.navigate("camera/$callbackKey")
        } else {
            cameraPermissionLauncher.launch(getCameraPermissions())
        }
    }

    fun requestAppGalleryAccess() {
        val key = "add_cat_gallery_${System.currentTimeMillis()}"
        viewModel.setCameraCallback(key) { imageId ->
            coroutineScope.launch {
                viewModel.getImageRepository().getImageById(imageId)?.let {
                    selectedImages = selectedImages + it
                }
            }
        }
        navController.navigate("gallerySelect/$key")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Dodaj kota",
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
            images = selectedImages,
            onGalleryClick = { requestGalleryAccess() },
            onCameraClick = { requestCameraAccess() },
            onAppGalleryClick = { requestAppGalleryAccess() },
            onImageRemove = { index ->
                val newList = selectedImages.toMutableList()
                newList.removeAt(index)
                selectedImages = newList
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Przycisk zapisu
        Button(
            onClick = {
                viewModel.addCat(name, breed, description, selectedImages.map { it.id })
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank()
        ) {
            Text("Zapisz Kota")
        }
    }
}
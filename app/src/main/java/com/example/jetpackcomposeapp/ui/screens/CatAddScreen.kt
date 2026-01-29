package com.example.jetpackcomposeapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.data.model.Image as CatImage
import com.example.jetpackcomposeapp.ui.utils.rememberGalleryLauncher
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CatAddScreen(navController: NavHostController, viewModel: CatViewModel) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf(listOf<CatImage>()) }
    val coroutineScope = rememberCoroutineScope()

    val galleryLauncher = rememberGalleryLauncher { uri ->
        coroutineScope.launch {
            viewModel.getImageRepository().saveImageFromGallery(uri)?.let {
                selectedImages = selectedImages + it
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Imię") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Rasa") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Opis") }, modifier = Modifier.fillMaxWidth().height(150.dp))

        Row(Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(Icons.Default.ThumbUp, null); Text(" Galeria")
            }
            Button(onClick = {
                val key = "add_cat_${System.currentTimeMillis()}"
                viewModel.setCameraCallback(key) { id ->
                    coroutineScope.launch {
                        viewModel.getImageRepository().getImageById(id)?.let { selectedImages = selectedImages + it }
                    }
                }
                navController.navigate("camera/$key")
            }) {
                Icon(Icons.Default.ThumbUp, null); Text(" Aparat")
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(selectedImages) { img ->
                AsyncImage(model = File(img.localPath), contentDescription = null, modifier = Modifier.size(100.dp))
            }
        }

        Button(
            onClick = {
                viewModel.addCat(name, breed, description, selectedImages.map { it.id })
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = name.isNotBlank()
        ) { Text("Zapisz Kota") }
    }
}
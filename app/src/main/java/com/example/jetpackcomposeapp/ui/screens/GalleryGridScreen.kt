package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import java.io.File

@Composable
fun GalleryGridScreen(navController: NavHostController, viewModel: CatViewModel) {
    val allImages by viewModel.allImages.collectAsState()

    if (allImages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Brak zdjęć w galerii!",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Dodaj zdjęcia kotów używając przycisków '+' w sekcji kotów",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
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
                            // Opcjonalnie: można dodać nawigację do szczegółowego podglądu
                            // Dla prostoty - na razie bez akcji kliknięcia
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
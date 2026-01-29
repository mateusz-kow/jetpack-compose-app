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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.viewmodel.CatViewModel

@Composable
fun GalleryGridScreen(navController: NavHostController, viewModel: CatViewModel = viewModel()) {
    val cats by viewModel.cats.collectAsState()

    val allImages = cats.flatMap { cat ->
        cat.images.map { imageUrl -> imageUrl to cat.id }
    }

    if (allImages.isEmpty()) {
        // Jeśli to zobaczysz, znaczy że problemem jest ViewModel / MockData
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Brak zdjęć w bazie danych!", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(allImages) { (imageUrl, catId) ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Zdjęcie kota",
                    // DODAJEMY PLACEHOLDER - jeśli go zobaczysz, znaczy że nie ma internetu
                    placeholder = null,
                    error = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { navController.navigate("detail/$catId") },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
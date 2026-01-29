package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CatDetailScreen(navController: NavHostController, catId: Int, catViewModel: CatViewModel = viewModel()) {
    val cat = catViewModel.getCatById(catId)

    if (cat != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Image
            Image(
                painter = rememberAsyncImagePainter(cat.images.firstOrNull() ?: ""),
                contentDescription = "Hero Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Cat Details
            Text(cat.name, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Text("Rasa: ${cat.breed}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Dodano: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(cat.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(cat.description, style = MaterialTheme.typography.bodyMedium)

            // Gallery
            Text("Galeria", style = MaterialTheme.typography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cat.images.size) { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Gallery Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    } else {
        Text("Kot nie został znaleziony", style = MaterialTheme.typography.bodyMedium)
    }
}

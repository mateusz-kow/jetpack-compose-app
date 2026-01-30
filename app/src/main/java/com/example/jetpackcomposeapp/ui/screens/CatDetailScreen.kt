package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.data.model.Cat
import com.example.jetpackcomposeapp.data.model.Image
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CatDetailScreen(navController: NavHostController, catId: Int, viewModel: CatViewModel) {
    var cat by remember { mutableStateOf<Cat?>(null) }
    var catImages by remember { mutableStateOf(listOf<Image>()) }

    LaunchedEffect(catId) {
        val loadedCat = viewModel.getCatById(catId)
        cat = loadedCat
        loadedCat?.let {
            catImages = viewModel.getImagesForCat(it)
        }
    }

    cat?.let { catData ->
        val scrollState = rememberScrollState()

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("edit/${catData.id}") }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                if (catImages.isNotEmpty()) {
                    val heroImageFile = File(catImages.first().localPath)
                    AsyncImage(
                        model = heroImageFile,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Brak zdjęcia głównego",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(catData.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(catData.breed, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(catData.createdAt)
                    Text("Dodano: $dateStr", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(catData.description, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(24.dp))

                    if (catImages.isNotEmpty()) {
                        Text("Galeria zdjęć (${catImages.size})", style = MaterialTheme.typography.titleLarge)

                        LazyRow(
                            contentPadding = PaddingValues(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(catImages) { index, image ->
                                val imageFile = File(image.localPath)
                                AsyncImage(
                                    model = imageFile,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            navController.navigate("viewer/${catData.id}/$index")
                                        },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        Text("Brak zdjęć", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Dodaj zdjęcia używając przycisku edycji",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

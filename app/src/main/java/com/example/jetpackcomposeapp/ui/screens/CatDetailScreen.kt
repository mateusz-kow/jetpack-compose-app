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
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CatDetailScreen(navController: NavHostController, catId: Int, viewModel: CatViewModel) {
    var cat by remember { mutableStateOf<Cat?>(null) }

    LaunchedEffect(catId) {
        cat = viewModel.getCatById(catId)
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
                // Hero Image
                AsyncImage(
                    model = catData.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(catData.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(catData.breed, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(catData.createdAt)
                    Text("Dodano: $dateStr", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(catData.description, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Galeria zdjęć", style = MaterialTheme.typography.titleLarge)

                    LazyRow(
                        contentPadding = PaddingValues(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(catData.images) { index, url ->
                            AsyncImage(
                                model = url,
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
                }
            }
        }
    } ?: run {
        // Loading state
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
}
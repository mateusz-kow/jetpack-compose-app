package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.R
import com.example.jetpackcomposeapp.data.model.Cat
import com.example.jetpackcomposeapp.data.model.Image
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatListScreen(navController: NavHostController, catViewModel: CatViewModel) {
    val cats = catViewModel.cats.collectAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State dla dialogu usuwania
    var showDeleteDialog by remember { mutableStateOf(false) }
    var catToDelete by remember { mutableStateOf<Cat?>(null) }

    // State dla obrazów kotów (cache)
    var catImages by remember { mutableStateOf(mapOf<Int, Image?>()) }

    // Ładowanie zdjęć głównych kotów
    LaunchedEffect(cats) {
        val imageMap = mutableMapOf<Int, Image?>()
        cats.forEach { cat ->
            if (cat.imageIds.isNotEmpty()) {
                val images = catViewModel.getImagesForCat(cat)
                imageMap[cat.id] = images.firstOrNull()
            } else {
                imageMap[cat.id] = null
            }
        }
        catImages = imageMap
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj kota")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(cats.size) { i ->
                val cat = cats[i]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                navController.navigate("detail/${cat.id}")
                            },
                            onLongClick = {
                                catToDelete = cat
                                showDeleteDialog = true
                            }
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Zdjęcie główne kota lub placeholder
                        val mainImage = catImages[cat.id]
                        if (mainImage != null) {
                            val imageFile = File(mainImage.localPath)
                            AsyncImage(
                                model = imageFile,
                                contentDescription = "Zdjęcie kota ${cat.name}",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.ic_launcher_foreground)
                            )
                        } else {
                            // Placeholder gdy brak zdjęcia
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Brak zdjęcia",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(cat.name, style = MaterialTheme.typography.titleMedium)
                            Text("Rasa: ${cat.breed}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Dodano: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(cat.createdAt)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog potwierdzenia usunięcia
    if (showDeleteDialog && catToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                catToDelete = null
            },
            title = {
                Text("Usuń kota")
            },
            text = {
                Text("Czy na pewno chcesz usunąć kota \"${catToDelete?.name}\"?\n\nTa akcja nie może zostać cofnięta.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        catToDelete?.let { cat ->
                            coroutineScope.launch {
                                // Nie usuwamy powiązanych zdjęć, bo zdjęcia w galeri to osobny byt od kotów
//                                val catImages = catViewModel.getImagesForCat(cat)
//                                catImages.forEach { image ->
//                                    catViewModel.getImageRepository().deleteImage(image)
//                                }
                                // Usuń kota
                                catViewModel.deleteCat(cat)
                            }
                        }
                        showDeleteDialog = false
                        catToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Usuń", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        catToDelete = null
                    }
                ) {
                    Text("Anuluj")
                }
            }
        )
    }
}

package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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

@Composable
fun CatEditScreen(navController: NavHostController, catId: Int, viewModel: CatViewModel) {
    var cat by remember { mutableStateOf<Cat?>(null) }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var images by remember { mutableStateOf(mutableListOf<String>()) }
    var showAddImageDialog by remember { mutableStateOf(false) }
    var newImageUrl by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    LaunchedEffect(catId) {
        val loadedCat = viewModel.getCatById(catId)
        cat = loadedCat
        loadedCat?.let {
            name = it.name
            breed = it.breed
            description = it.description
            images = it.images.toMutableList()
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

            // Sekcja zdjęć
            Text(
                text = "Zdjęcia",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista zdjęć z możliwością usuwania i dodawania
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Przycisk dodawania zdjęcia
                item {
                    Card(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { showAddImageDialog = true },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Dodaj zdjęcie",
                                    modifier = Modifier.size(32.dp)
                                )
                                Text("Dodaj zdjęcie", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Istniejące zdjęcia
                itemsIndexed(images) { index, imageUrl ->
                    Box(modifier = Modifier.size(120.dp)) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Przycisk usuwania
                        IconButton(
                            onClick = {
                                val newList = images.toMutableList()
                                newList.removeAt(index)
                                images = newList
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Usuń",
                                        tint = MaterialTheme.colorScheme.onError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

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
                                    images = images.toList()
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

        // Dialog dodawania zdjęcia
        if (showAddImageDialog) {
            AlertDialog(
                onDismissRequest = { showAddImageDialog = false },
                title = { Text("Dodaj zdjęcie") },
                text = {
                    Column {
                        Text("Wprowadź URL zdjęcia lub wybierz przykład:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newImageUrl,
                            onValueChange = { newImageUrl = it },
                            label = { Text("URL zdjęcia") },
                            placeholder = { Text("https://przykład.com/zdjęcie.jpg") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Przykładowe zdjęcia:", style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = { newImageUrl = "https://placekitten.com/400/400" }) {
                                Text("Kot 1", style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { newImageUrl = "https://placekitten.com/450/450" }) {
                                Text("Kot 2", style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { newImageUrl = "https://placekitten.com/500/500" }) {
                                Text("Kot 3", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newImageUrl.isNotBlank()) {
                                val newList = images.toMutableList()
                                newList.add(newImageUrl)
                                images = newList
                                newImageUrl = ""
                                showAddImageDialog = false
                            }
                        },
                        enabled = newImageUrl.isNotBlank()
                    ) {
                        Text("Dodaj")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        newImageUrl = ""
                        showAddImageDialog = false
                    }) {
                        Text("Anuluj")
                    }
                }
            )
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
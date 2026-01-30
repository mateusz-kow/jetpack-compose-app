package com.example.jetpackcomposeapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.data.model.Image
import java.io.File

@Composable
fun ImagePickerSection(
    images: List<Image>,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onAppGalleryClick: () -> Unit,
    onImageRemove: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Zdjęcia",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { onGalleryClick() },
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
                                Icons.Default.PhotoLibrary,
                                contentDescription = "Wybierz z galerii urządzenia",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Galeria", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { onCameraClick() },
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
                                Icons.Default.CameraAlt,
                                contentDescription = "Zrób zdjęcie aparatem",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Aparat", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { onAppGalleryClick() },
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
                                Icons.Default.Collections,
                                contentDescription = "Wybierz z galerii aplikacji",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Z App", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            itemsIndexed(images) { index, image ->
                Box(modifier = Modifier.size(120.dp)) {
                    val imageFile = File(image.localPath)
                    AsyncImage(
                        model = imageFile,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(onLongPress = {
                                    onImageRemove(index)
                                })
                            },
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { onImageRemove(index) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                            .padding(4.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Close,
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
    }
}



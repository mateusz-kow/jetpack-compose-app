package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryViewerScreen(
    navController: NavHostController,
    initialIndex: Int,
    viewModel: CatViewModel
) {
    val allImages by viewModel.allImages.collectAsState()

    if (allImages.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = initialIndex.coerceIn(0, allImages.size - 1),
            pageCount = { allImages.size }
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // Pager do przesuwania zdjęć lewo-prawo
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp,
                userScrollEnabled = true // Zapewniam że swipe jest włączony
            ) { pageIndex ->
                // Logika Zoomu dla każdego zdjęcia z osobna
                var scale by remember { mutableFloatStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }

                val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                    scale = (scale * zoomChange).coerceIn(1f, 5f)
                    offset = if (scale > 1f) {
                        offset + offsetChange
                    } else {
                        Offset.Zero
                    }
                }

                val imageFile = File(allImages[pageIndex].localPath)
                AsyncImage(
                    model = imageFile,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        // Tylko dodaj transformable gdy zoom > 1, żeby nie kolidować z pager
                        .let { modifier ->
                            if (scale > 1.05f) {
                                modifier.transformable(state = state)
                            } else {
                                modifier
                            }
                        },
                    contentScale = ContentScale.Fit
                )
            }

            // Przycisk zamknięcia na górze
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz", tint = Color.White)
            }

            // Wskaźnik pozycji
            if (allImages.size > 1) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${allImages.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                        .padding(8.dp)
                )
            }

            // Informacje o zdjęciu
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.3f), MaterialTheme.shapes.small),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    val currentImage = allImages[pagerState.currentPage]
                    Text(
                        text = "Źródło: ${currentImage.source.name}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Dodano: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(currentImage.createdAt)}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    } else {
        // Loading state
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}

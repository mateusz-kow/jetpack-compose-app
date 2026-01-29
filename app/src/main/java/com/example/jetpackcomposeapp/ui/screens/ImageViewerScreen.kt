package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jetpackcomposeapp.viewmodel.CatViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewerScreen(
    navController: NavHostController,
    catId: Int,
    initialIndex: Int,
    viewModel: CatViewModel = viewModel()
) {
    val cat = viewModel.getCatById(catId) ?: return
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { cat.images.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Pager do przesuwania zdjęć lewo-prawo
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = 16.dp
        ) { pageIndex ->
            // Logika Zoomu dla każdego zdjęcia z osobna
            var scale by remember { mutableFloatStateOf(1f) }
            val state = rememberTransformableState { zoomChange, _, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 5f) // Limit zoomu od 1x do 5x
            }

            AsyncImage(
                model = cat.images[pageIndex],
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                    .transformable(state = state),
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
            Icon(Icons.Default.Close, contentDescription = "Zamknij", tint = Color.White)
        }
    }
}
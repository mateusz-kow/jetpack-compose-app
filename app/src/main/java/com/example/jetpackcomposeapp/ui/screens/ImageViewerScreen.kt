package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    viewModel: CatViewModel
) {
    var catImages by remember { mutableStateOf(listOf<com.example.jetpackcomposeapp.data.model.Image>()) }

    LaunchedEffect(catId) {
        val cat = viewModel.getCatById(catId)
        cat?.let {
            catImages = viewModel.getImagesForCat(it)
        }
    }

    if (catImages.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = initialIndex.coerceIn(0, catImages.size - 1),
            pageCount = { catImages.size }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp,
                userScrollEnabled = true
            ) { pageIndex ->

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

                val imageFile = java.io.File(catImages[pageIndex].localPath)
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
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
//                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Zamknij", tint = Color.White)
        }

        // Wskaźnik pozycji
        if (catImages.size > 1) {
            Text(
                text = "${pagerState.currentPage + 1} / ${catImages.size}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
//                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                    .padding(8.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}
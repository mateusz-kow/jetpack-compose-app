package com.example.jetpackcomposeapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    navController: NavHostController,
    catViewModel: CatViewModel,
    callbackKey: String? = null // Klucz dla callback system
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashEnabled by remember { mutableStateOf(false) }
    var captureInProgress by remember { mutableStateOf(false) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            context = context,
            lifecycleOwner = lifecycleOwner,
            lensFacing = lensFacing,
            imageCapture = imageCapture,
            flashEnabled = flashEnabled
        )

        // Top Bar with back button and flash toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.White, CircleShape)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Wstecz",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.White, CircleShape)
            ) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "Przełącz kamerę",
                    tint = Color.White
                )
            }
        }

        // Bottom controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(64.dp))

            // Capture button
            FloatingActionButton(
                onClick = {
                    if (!captureInProgress) {
                        captureInProgress = true
                        captureImage(
                            imageCapture = imageCapture,
                            context = context,
                            executor = cameraExecutor,
                            onImageCaptured = { imagePath ->
                                coroutineScope.launch {
                                    val imageRepository = catViewModel.getImageRepository()

                                    // Konwertuj File na Bitmap i zapisz przez repository
                                    val bitmap = BitmapFactory.decodeFile(imagePath)
                                    val savedImage = imageRepository.saveImageFromCamera(bitmap)

                                    captureInProgress = false

                                    if (savedImage != null) {
                                        // Sprawdź czy jest callback w ViewModel
                                        callbackKey?.let { key ->
                                            val callback = catViewModel.getCameraCallback(key)
                                            callback?.invoke(savedImage.id)
                                            catViewModel.clearCameraCallback(key)
                                        }
                                        navController.popBackStack()
                                    }
                                }
                            },
                            onError = {
                                captureInProgress = false
                            }
                        )
                    }
                },
                modifier = Modifier.size(80.dp),
                containerColor = Color.White
            ) {
                if (captureInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Zrób zdjęcie",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.size(64.dp))
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    lensFacing: Int,
    imageCapture: ImageCapture,
    flashEnabled: Boolean
) {
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(lensFacing, flashEnabled) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                // Ustaw flash
                camera.cameraControl.enableTorch(flashEnabled)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

private fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    executor: ExecutorService,
    onImageCaptured: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
        File(context.cacheDir, "temp_camera_${System.currentTimeMillis()}.jpg")
    ).build()

    imageCapture.takePicture(
        outputFileOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let { uri ->
                    onImageCaptured(uri.path ?: "")
                }
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

package com.example.jetpackcomposeapp.ui.screens

import android.graphics.BitmapFactory
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    navController: NavHostController,
    catViewModel: CatViewModel,
    callbackKey: String? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var captureInProgress by remember { mutableStateOf(false) }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val previewView = remember { PreviewView(context) }

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(lensFacing) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (e: Exception) { e.printStackTrace() }
        }

        // UI Controls
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.statusBarsPadding().padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz", tint = Color.White)
        }

        IconButton(
            onClick = { lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK },
            modifier = Modifier.statusBarsPadding().padding(16.dp).align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.ThumbUp, contentDescription = "Zmień kamerę", tint = Color.White)
        }

        FloatingActionButton(
            onClick = {
                if (!captureInProgress) {
                    captureInProgress = true
                    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                    val options = ImageCapture.OutputFileOptions.Builder(file).build()

                    imageCapture.takePicture(options, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            coroutineScope.launch {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                val savedImage = catViewModel.getImageRepository().saveImageFromCamera(bitmap)
                                if (savedImage != null && callbackKey != null) {
                                    catViewModel.getCameraCallback(callbackKey)?.invoke(savedImage.id)
                                    catViewModel.clearCameraCallback(callbackKey)
                                }
                                captureInProgress = false
                                navController.popBackStack()
                            }
                        }
                        override fun onError(e: ImageCaptureException) { captureInProgress = false }
                    })
                }
            },
            modifier = Modifier.padding(bottom = 32.dp).size(80.dp).align(Alignment.BottomCenter),
            shape = CircleShape,
            containerColor = Color.White
        ) {
            if (captureInProgress) CircularProgressIndicator()
            else Icon(Icons.Default.ThumbUp, contentDescription = "Foto", tint = Color.Black)
        }
    }
}
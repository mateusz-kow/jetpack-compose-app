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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
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
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz", tint = Color.White)
        }

        IconButton(
            onClick = {
                lensFacing =
                    if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
            },
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.Cameraswitch, contentDescription = "Zmień kamerę", tint = Color.White)
        }

        FloatingActionButton(
            onClick = {
                if (!captureInProgress) {
                    captureInProgress = true
                    val tempFile =
                        File(context.cacheDir, "temp_camera_${System.currentTimeMillis()}.jpg")
                    val options = ImageCapture.OutputFileOptions.Builder(tempFile).build()

                    imageCapture.takePicture(
                        options,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                coroutineScope.launch {
                                    try {
                                        val savedImage = withContext(Dispatchers.IO) {
                                            val bitmap =
                                                BitmapFactory.decodeFile(tempFile.absolutePath)
                                            catViewModel.getImageRepository()
                                                .saveImageFromCamera(bitmap)
                                        }

                                        savedImage?.let { img ->
                                            callbackKey?.let { key ->
                                                catViewModel.getCameraCallback(key)?.invoke(img.id)
                                                catViewModel.clearCameraCallback(key)
                                            }
                                        }
                                    } catch (t: Throwable) {
                                        t.printStackTrace()
                                    } finally {

                                        captureInProgress = false
                                        try {
                                            tempFile.delete()
                                        } catch (_: Exception) {
                                        }
                                        navController.popBackStack()
                                    }
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                exception.printStackTrace()
                                captureInProgress = false
                            }
                        })
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .size(80.dp)
                .align(Alignment.BottomCenter),
            shape = CircleShape,
            containerColor = Color.White
        ) {
            if (captureInProgress) CircularProgressIndicator(color = Color.Black)
            else Icon(Icons.Default.CameraAlt, contentDescription = "Foto", tint = Color.Black)
        }
    }
}
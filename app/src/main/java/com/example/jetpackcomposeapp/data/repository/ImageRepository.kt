package com.example.jetpackcomposeapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.jetpackcomposeapp.data.local.AppDatabase
import com.example.jetpackcomposeapp.data.local.ImageDao
import com.example.jetpackcomposeapp.data.model.Image
import com.example.jetpackcomposeapp.data.model.ImageSource
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.UUID

class ImageRepository(private val context: Context, private val imageDao: ImageDao) {

    fun getAllImages(): Flow<List<Image>> = imageDao.getAllImages()

    suspend fun getImageById(id: Int): Image? = imageDao.getImageById(id)

    suspend fun getImagesByIds(ids: List<Int>): List<Image> = imageDao.getImagesByIds(ids)

    suspend fun getImageCount(): Int = imageDao.getImageCount()

    suspend fun saveImageFromGallery(uri: Uri): Image? {
        return try {
            val fileName = "image_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, "images")

            if (!file.exists()) {
                file.mkdirs()
            }

            val imageFile = File(file, fileName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                FileOutputStream(imageFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                }
            }

            val image = Image(
                localPath = imageFile.absolutePath,
                fileName = fileName,
                createdAt = Date(),
                source = ImageSource.GALLERY
            )

            val imageId = imageDao.insertImage(image)
            image.copy(id = imageId.toInt())

        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveImageFromCamera(bitmap: Bitmap): Image? {
        return try {
            val fileName = "camera_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, "images")

            if (!file.exists()) {
                file.mkdirs()
            }

            val imageFile = File(file, fileName)

            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }

            val image = Image(
                localPath = imageFile.absolutePath,
                fileName = fileName,
                createdAt = Date(),
                source = ImageSource.CAMERA
            )

            val imageId = imageDao.insertImage(image)
            image.copy(id = imageId.toInt())

        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteImage(image: Image): Boolean {
        return try {
            val file = File(image.localPath)
            if (file.exists()) {
                file.delete()
            }

            imageDao.deleteImage(image)
            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getImageFile(image: Image): File {
        return File(image.localPath)
    }
}

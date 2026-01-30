package com.example.jetpackcomposeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeapp.data.local.AppDatabase
import com.example.jetpackcomposeapp.data.local.CatDao
import com.example.jetpackcomposeapp.data.local.DatabaseModule
import com.example.jetpackcomposeapp.data.model.Cat
import com.example.jetpackcomposeapp.data.model.Image
import com.example.jetpackcomposeapp.data.repository.ImageRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class CatViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = DatabaseModule.getDatabase(application)
    private val catDao: CatDao = database.catDao()
    private val imageRepository: ImageRepository = ImageRepository(application, database.imageDao())

    val cats: StateFlow<List<Cat>> = catDao.getAllCats()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allImages: StateFlow<List<Image>> = imageRepository.getAllImages()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addCat(name: String, breed: String, description: String, imageIds: List<Int> = emptyList()) {
        viewModelScope.launch {
            val newCat = Cat(
                name = name,
                breed = breed,
                description = description,
                createdAt = Date(),
                imageIds = imageIds
            )
            catDao.insertCat(newCat)
        }
    }

    fun updateCat(updatedCat: Cat) {
        viewModelScope.launch {
            catDao.updateCat(updatedCat)
        }
    }

    fun deleteCat(cat: Cat) {
        viewModelScope.launch {
            catDao.deleteCat(cat)
        }
    }

    suspend fun addImageToCat(catId: Int, imageId: Int) {
        val cat = catDao.getCatById(catId)
        cat?.let {
            val updatedImageIds = it.imageIds.toMutableList().apply { add(imageId) }
            updateCat(it.copy(imageIds = updatedImageIds))
        }
    }

    private val _cameraCallbacks = mutableMapOf<String, (Int) -> Unit>()

    fun setCameraCallback(key: String, callback: (Int) -> Unit) {
        _cameraCallbacks[key] = callback
    }

    fun getCameraCallback(key: String): ((Int) -> Unit)? {
        return _cameraCallbacks[key]
    }

    fun clearCameraCallback(key: String) {
        _cameraCallbacks.remove(key)
    }

    suspend fun getCatById(catId: Int): Cat? {
        return catDao.getCatById(catId)
    }

    suspend fun getImagesForCat(cat: Cat): List<Image> {
        return imageRepository.getImagesByIds(cat.imageIds)
    }

    fun getImageRepository(): ImageRepository = imageRepository
}

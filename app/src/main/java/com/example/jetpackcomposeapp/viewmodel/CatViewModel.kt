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

    init {
        // Dodaj przykładowe dane przy pierwszym uruchomieniu (bez zdjęć)
        viewModelScope.launch {
            val existingCats = catDao.getAllCats().first()
            if (existingCats.isEmpty()) {
                initializeWithSampleData()
            }
        }
    }

    private suspend fun initializeWithSampleData() {
        val sampleCats = listOf(
            Cat(
                name = "Whiskers",
                breed = "Persian",
                description = "A fluffy and friendly Persian cat with beautiful long fur. Loves to be petted and enjoys sunny spots by the window.",
                createdAt = Date(),
                imageIds = emptyList() // Puste - użytkownik doda własne zdjęcia
            ),
            Cat(
                name = "Mittens",
                breed = "Siamese",
                description = "A curious and vocal Siamese cat with striking blue eyes. Very intelligent and loves interactive toys.",
                createdAt = Date(System.currentTimeMillis() - 86400000),
                imageIds = emptyList()
            ),
            Cat(
                name = "Shadow",
                breed = "Maine Coon",
                description = "A large and gentle Maine Coon cat with impressive size and gentle nature. Great with children and other pets.",
                createdAt = Date(System.currentTimeMillis() - 172800000),
                imageIds = emptyList()
            )
        )

        sampleCats.forEach { cat ->
            catDao.insertCat(cat)
        }
    }

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

    suspend fun getCatById(catId: Int): Cat? {
        return catDao.getCatById(catId)
    }

    suspend fun getImagesForCat(cat: Cat): List<Image> {
        return imageRepository.getImagesByIds(cat.imageIds)
    }

    fun getImageRepository(): ImageRepository = imageRepository
}

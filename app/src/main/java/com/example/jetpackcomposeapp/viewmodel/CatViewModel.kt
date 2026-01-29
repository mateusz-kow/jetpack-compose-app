package com.example.jetpackcomposeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeapp.data.local.AppDatabase
import com.example.jetpackcomposeapp.data.local.CatDao
import com.example.jetpackcomposeapp.data.local.DatabaseModule
import com.example.jetpackcomposeapp.data.model.Cat
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class CatViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = DatabaseModule.getDatabase(application)
    private val catDao: CatDao = database.catDao()

    val cats: StateFlow<List<Cat>> = catDao.getAllCats()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Dodaj przykładowe dane przy pierwszym uruchomieniu
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
                images = listOf(
                    "https://placekitten.com/800/600",
                    "https://placekitten.com/801/601",
                    "https://placekitten.com/802/602"
                )
            ),
            Cat(
                name = "Mittens",
                breed = "Siamese",
                description = "A curious and vocal Siamese cat with striking blue eyes. Very intelligent and loves interactive toys.",
                createdAt = Date(System.currentTimeMillis() - 86400000), // 1 day ago
                images = listOf(
                    "https://placekitten.com/803/603",
                    "https://placekitten.com/804/604"
                )
            ),
            Cat(
                name = "Shadow",
                breed = "Maine Coon",
                description = "A large and gentle Maine Coon cat with impressive size and gentle nature. Great with children and other pets.",
                createdAt = Date(System.currentTimeMillis() - 172800000), // 2 days ago
                images = listOf(
                    "https://placekitten.com/805/605",
                    "https://placekitten.com/806/606",
                    "https://placekitten.com/807/607",
                    "https://placekitten.com/808/608"
                )
            )
        )

        sampleCats.forEach { cat ->
            catDao.insertCat(cat)
        }
    }

    fun addCat(name: String, breed: String, description: String, images: List<String> = emptyList()) {
        viewModelScope.launch {
            val newCat = Cat(
                name = name,
                breed = breed,
                description = description,
                createdAt = Date(),
                images = images
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
}

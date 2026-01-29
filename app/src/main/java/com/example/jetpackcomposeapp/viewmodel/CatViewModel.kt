package com.example.jetpackcomposeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeapp.data.model.Cat
import com.example.jetpackcomposeapp.data.mock.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

class CatViewModel : ViewModel() {
    private val _cats = MutableStateFlow(MockData.cats)
    val cats: StateFlow<List<Cat>> = _cats

    fun addCat(name: String) {
        val newCat = Cat(
            id = _cats.value.size + 1,
            name = name,
            createdAt = Date()
        )
        _cats.value = _cats.value + newCat
    }

    fun updateCat(id: Int, name: String) {
        _cats.value = _cats.value.map {
            if (it.id == id) it.copy(name = name) else it
        }
    }

    fun deleteCat(id: Int) {
        _cats.value = _cats.value.filter { it.id != id }
    }
}

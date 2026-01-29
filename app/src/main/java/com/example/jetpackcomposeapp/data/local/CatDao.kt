package com.example.jetpackcomposeapp.data.local

import androidx.room.*
import com.example.jetpackcomposeapp.data.model.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
    @Query("SELECT * FROM cats ORDER BY createdAt DESC")
    fun getAllCats(): Flow<List<Cat>>

    @Query("SELECT * FROM cats WHERE id = :id")
    suspend fun getCatById(id: Int): Cat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCat(cat: Cat): Long

    @Update
    suspend fun updateCat(cat: Cat): Int

    @Delete
    suspend fun deleteCat(cat: Cat): Int
}
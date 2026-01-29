package com.example.jetpackcomposeapp.data.local

import androidx.room.*
import com.example.jetpackcomposeapp.data.model.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM images ORDER BY createdAt DESC")
    fun getAllImages(): Flow<List<Image>>

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getImageById(id: Int): Image?

    @Query("SELECT * FROM images WHERE id IN (:ids)")
    suspend fun getImagesByIds(ids: List<Int>): List<Image>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image): Long

    @Update
    suspend fun updateImage(image: Image): Int

    @Delete
    suspend fun deleteImage(image: Image): Int

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun deleteImageById(id: Int): Int

    @Query("SELECT COUNT(*) FROM images")
    suspend fun getImageCount(): Int
}

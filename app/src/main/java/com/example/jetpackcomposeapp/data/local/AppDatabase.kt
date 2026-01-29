package com.example.jetpackcomposeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jetpackcomposeapp.data.model.Cat
import com.example.jetpackcomposeapp.data.model.Image

@Database(
    entities = [Cat::class, Image::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
    abstract fun imageDao(): ImageDao
}
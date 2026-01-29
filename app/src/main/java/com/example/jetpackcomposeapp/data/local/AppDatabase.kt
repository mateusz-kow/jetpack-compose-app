package com.example.jetpackcomposeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jetpackcomposeapp.data.model.Cat

@Database(entities = [Cat::class], version = 1)
@TypeConverters(DateConverter::class) // Rejestrujemy nasz własny typ!
abstract class AppDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
}
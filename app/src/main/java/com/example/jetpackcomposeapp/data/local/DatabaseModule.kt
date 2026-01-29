package com.example.jetpackcomposeapp.data.local

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "cat_database"
            )
            .fallbackToDestructiveMigration() // Tymczasowo - usuwa i tworzy nową bazę przy zmianie schematu
            .build()
            INSTANCE = instance
            instance
        }
    }
}

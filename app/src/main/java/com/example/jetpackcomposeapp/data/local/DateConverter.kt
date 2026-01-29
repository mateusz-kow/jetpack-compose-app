package com.example.jetpackcomposeapp.data.local

import androidx.room.TypeConverter
import com.example.jetpackcomposeapp.data.model.ImageSource
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fromIntList(value: String?): List<Int>? {
        return value?.split(",")?.mapNotNull {
            it.trim().toIntOrNull()
        }?.filter { it > 0 }
    }

    @TypeConverter
    fun intListToString(list: List<Int>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fromImageSource(source: ImageSource): String {
        return source.name
    }

    @TypeConverter
    fun toImageSource(source: String): ImageSource {
        return try {
            ImageSource.valueOf(source)
        } catch (e: IllegalArgumentException) {
            ImageSource.GALLERY
        }
    }
}
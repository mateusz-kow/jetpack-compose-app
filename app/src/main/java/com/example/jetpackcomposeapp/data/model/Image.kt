package com.example.jetpackcomposeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val localPath: String,
    val fileName: String,
    val createdAt: Date,
    val source: ImageSource = ImageSource.GALLERY
)

enum class ImageSource {
    GALLERY,
    CAMERA
}

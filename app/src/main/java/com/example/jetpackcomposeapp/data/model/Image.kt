package com.example.jetpackcomposeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val localPath: String, // Ścieżka do lokalnego pliku
    val fileName: String, // Nazwa pliku
    val createdAt: Date, // Kiedy obraz został dodany do aplikacji
    val source: ImageSource = ImageSource.GALLERY // Skąd pochodzi obraz
)

enum class ImageSource {
    GALLERY,    // Z galerii użytkownika
    CAMERA      // Z aparatu (na przyszłość)
}

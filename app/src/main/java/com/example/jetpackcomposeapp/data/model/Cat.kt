package com.example.jetpackcomposeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cats")
data class Cat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val breed: String,
    val description: String,
    val createdAt: Date,
    val imageIds: List<Int>
)
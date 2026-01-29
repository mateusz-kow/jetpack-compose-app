package com.example.jetpackcomposeapp.data.model

import java.util.Date

data class Cat(
    val id: Int,
    val name: String,
    val createdAt: Date,
    val breed: String,
    val description: String,
    val images: List<String>
)

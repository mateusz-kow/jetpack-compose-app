package com.example.jetpackcomposeapp.data.mock

import com.example.jetpackcomposeapp.data.model.Cat
import java.util.Date

object MockData {
    val cats = listOf(
        Cat(
            id = 1,
            name = "Whiskers",
            createdAt = Date(),
            breed = "Persian",
            description = "A fluffy and friendly Persian cat.",
            images = listOf("https://http.cat/200", "https://http.cat/200")
        ),
        Cat(
            id = 2,
            name = "Mittens",
            createdAt = Date(),
            breed = "Siamese",
            description = "A curious and vocal Siamese cat.",
            images = listOf("https://http.cat/200", "https://http.cat/200")
        ),
        Cat(
            id = 3,
            name = "Shadow",
            createdAt = Date(),
            breed = "Maine Coon",
            description = "A large and gentle Maine Coon cat.",
            images = listOf("https://http.cat/200", "https://http.cat/200")
        )
    )
}

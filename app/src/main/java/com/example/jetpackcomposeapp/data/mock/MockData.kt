package com.example.jetpackcomposeapp.data.mock

import com.example.jetpackcomposeapp.data.model.Cat
import java.util.Date

object MockData {
    val cats = listOf(
        Cat(1, "Whiskers", Date()),
        Cat(2, "Mittens", Date()),
        Cat(3, "Shadow", Date())
    )
}

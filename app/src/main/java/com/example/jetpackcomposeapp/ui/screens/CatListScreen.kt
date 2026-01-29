package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.ui.navigation.NavigationItem

@Composable
fun CatListScreen(navController: NavHostController) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Button(onClick = { navController.navigate(NavigationItem.Detail.route) }) {
            Text(text = "Go to Cat Detail")
        }
    }
}

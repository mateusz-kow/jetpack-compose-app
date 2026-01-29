package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.R
import com.example.jetpackcomposeapp.ui.navigation.NavigationItem

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Author Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Author Avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Autor: Jan Kowalski", style = MaterialTheme.typography.titleMedium)
                    Text("GitHub: github.com/jankowalski", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // About App Section
        Text(
            text = "Twoje centrum dowodzenia kocim światem",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        // Statistics Section
        Text(
            text = "Masz już 5 kotów i 124 zdjęcia",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        // Call to Action
        Button(onClick = { navController.navigate("list") }) {
            Text("Dodaj pierwszego kota")
        }
    }
}

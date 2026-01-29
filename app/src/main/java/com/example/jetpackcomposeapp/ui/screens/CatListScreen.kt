package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.R
import com.example.jetpackcomposeapp.ui.navigation.NavigationItem
import com.example.jetpackcomposeapp.viewmodel.CatViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CatListScreen(navController: NavHostController, catViewModel: CatViewModel = viewModel()) {
    val cats = catViewModel.cats.collectAsState().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add new cat */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Cat")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(cats.size) { i ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detail/${cats[i].id}") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Cat Image",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(cats[i].name, style = MaterialTheme.typography.titleMedium)
                            Text("Rasa: ${cats[i].breed}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Dodano: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(cats[i].createdAt)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

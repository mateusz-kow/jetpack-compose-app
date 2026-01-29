package com.example.jetpackcomposeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.jetpackcomposeapp.data.model.Cat
import com.example.jetpackcomposeapp.viewmodel.CatViewModel

@Composable
fun CatEditScreen(navController: NavHostController, catId: Int, viewModel: CatViewModel) {
    var cat by remember { mutableStateOf<Cat?>(null) }

    LaunchedEffect(catId) {
        cat = viewModel.getCatById(catId)
    }

    cat?.let { catData ->
        var name by remember { mutableStateOf(catData.name) }
        var breed by remember { mutableStateOf(catData.breed) }
        var description by remember { mutableStateOf(catData.description) }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Imię kota") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Rasa") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateCat(catData.copy(name = name, breed = breed, description = description))
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz zmiany")
            }
        }
    } ?: run {
        // Loading state
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    }
}
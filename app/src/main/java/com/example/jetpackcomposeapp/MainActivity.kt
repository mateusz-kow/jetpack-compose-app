package com.example.jetpackcomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeapp.ui.navigation.NavGraph
import com.example.jetpackcomposeapp.ui.theme.ThemeManager
import com.example.jetpackcomposeapp.ui.theme.getColorScheme
import com.example.jetpackcomposeapp.viewmodel.CatViewModel

class MainActivity : ComponentActivity() {
    private val themeManager = ThemeManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Załaduj zapisany motyw
        themeManager.loadTheme(this)

        setContent {
            MyAppTheme(themeManager = themeManager) {
                Surface(modifier = Modifier) {
                    val navController = rememberNavController()
                    val catViewModel: CatViewModel = viewModel()
                    NavGraph(
                        navController = navController,
                        catViewModel = catViewModel,
                        themeManager = themeManager
                    )
                }
            }
        }
    }
}

@Composable
fun MyAppTheme(
    themeManager: ThemeManager,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = getColorScheme(themeManager.currentTheme, isDarkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}


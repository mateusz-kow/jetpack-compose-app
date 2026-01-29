package com.example.jetpackcomposeapp.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemePreferences {
    private const val PREFS_NAME = "app_theme_prefs"
    private const val THEME_KEY = "app_theme"

    fun getTheme(context: Context): AppTheme {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val themeName = prefs.getString(THEME_KEY, AppTheme.DEFAULT.name) ?: AppTheme.DEFAULT.name
        return try {
            AppTheme.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            AppTheme.DEFAULT
        }
    }

    fun setTheme(context: Context, theme: AppTheme) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(THEME_KEY, theme.name).apply()
    }
}

enum class AppTheme {
    DEFAULT,
    OCEAN,
    FOREST,
    SUNSET
}

class ThemeManager {
    var currentTheme by mutableStateOf(AppTheme.DEFAULT)
        private set

    fun setTheme(theme: AppTheme) {
        currentTheme = theme
    }

    fun loadTheme(context: Context) {
        currentTheme = ThemePreferences.getTheme(context)
    }
}

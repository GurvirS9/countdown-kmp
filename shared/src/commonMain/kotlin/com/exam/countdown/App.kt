package com.exam.countdown

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.exam.countdown.navigation.AppNavigation
import com.exam.countdown.ui.settings.AppSettings
import com.exam.countdown.ui.settings.ThemeMode
import com.exam.countdown.ui.theme.AppTheme

@Composable
fun App() {
    val themeMode by AppSettings.themeMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
    }
    AppTheme(darkTheme = isDark) {
        AppNavigation()
    }
}
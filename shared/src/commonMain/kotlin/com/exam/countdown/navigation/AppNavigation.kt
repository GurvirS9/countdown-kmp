package com.exam.countdown.navigation

import androidx.compose.runtime.*
import com.exam.countdown.model.Exam
import com.exam.countdown.ui.screens.*

sealed class Screen {
    object Home : Screen()
    object AddExam : Screen()
    data class EditExam(val exam: Exam) : Screen()
    object Import : Screen()
    object Stats : Screen()
    object Settings : Screen()
}

@Composable
fun AppNavigation() {
    val stack = remember { mutableStateListOf<Screen>(Screen.Home) }

    when (val screen = stack.last()) {
        is Screen.Home -> HomeScreen(
            onNavigateToAdd = { stack.add(Screen.AddExam) },
            onNavigateToImport = { stack.add(Screen.Import) },
            onNavigateToSettings = { stack.add(Screen.Settings) },
            onNavigateToEdit = { exam -> stack.add(Screen.EditExam(exam)) },
        )
        is Screen.AddExam -> AddExamScreen(
            editingExam = null,
            onBack = { stack.removeLastOrNull() },
        )
        is Screen.EditExam -> AddExamScreen(
            editingExam = screen.exam,
            onBack = { stack.removeLastOrNull() },
        )
        is Screen.Import -> ImportScreen(onBack = { stack.removeLastOrNull() })
        is Screen.Stats -> StatsScreen(onBack = { stack.removeLastOrNull() })
        is Screen.Settings -> SettingsScreen(onBack = { stack.removeLastOrNull() })
    }
}


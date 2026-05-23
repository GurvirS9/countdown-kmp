package com.exam.countdown

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.exam.countdown.di.jvmPlatformModule
import com.exam.countdown.di.viewModelModule
import org.koin.core.context.startKoin

fun main() = application {
    // Initialize Koin for the Desktop target
    startKoin {
        modules(jvmPlatformModule, viewModelModule)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Countdown",
        icon = painterResource("icon.png"),
    ) {
        App()
    }
}
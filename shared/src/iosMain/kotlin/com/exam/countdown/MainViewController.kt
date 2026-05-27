package com.exam.countdown

import androidx.compose.ui.window.ComposeUIViewController
import com.exam.countdown.di.initKoin

fun MainViewController() = ComposeUIViewController { App() }

object KoinHelper {
    fun initKoin() {
        com.exam.countdown.di.initKoin()
    }
}
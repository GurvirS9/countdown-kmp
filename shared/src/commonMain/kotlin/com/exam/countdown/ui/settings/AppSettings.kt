package com.exam.countdown.ui.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Which time-format to use in countdown cards. */
enum class TimeFormat {
    /** Show DD:HH:MM:SS */
    FULL,
    /** Show HH:MM:SS (no days column) */
    HHMMSS,
}

/** The user's preferred colour scheme override. */
enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

/** App-wide settings singleton, held in memory for the session. */
object AppSettings {
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _timeFormat = MutableStateFlow(TimeFormat.FULL)
    val timeFormat: StateFlow<TimeFormat> = _timeFormat.asStateFlow()

    fun setThemeMode(mode: ThemeMode) = _themeMode.update { mode }
    fun setTimeFormat(format: TimeFormat) = _timeFormat.update { format }
}

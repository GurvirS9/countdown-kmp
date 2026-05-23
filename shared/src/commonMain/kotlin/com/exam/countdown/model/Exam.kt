package com.exam.countdown.model

import kotlinx.serialization.Serializable

/** The timezone all times are stored and displayed in. */
const val APP_TIMEZONE = "Asia/Kolkata"

/** Predefined color tags for exam cards. */
val COLOR_TAGS = listOf(
    "#6750A4", // Purple (default)
    "#B5264B", // Red
    "#1565C0", // Blue
    "#2E7D32", // Green
    "#E65100", // Orange
    "#00695C", // Teal
    "#AD1457", // Pink
    "#4527A0", // Deep Purple
    "#00838F", // Cyan
    "#F9A825", // Amber
)

@Serializable
data class Exam(
    val id: Long = 0L,
    val name: String,
    val subject: String,
    val semester: String,
    val examTimeEpochMillis: Long,
    val timezone: String = APP_TIMEZONE,
    val colorTag: String = COLOR_TAGS.first(),
    val archived: Boolean = false,
)

enum class ExamStatus { UPCOMING, STARTED, COMPLETED }

data class CountdownState(
    val days: Long = 0L,
    val hours: Long = 0L,
    val minutes: Long = 0L,
    val seconds: Long = 0L,
    val status: ExamStatus = ExamStatus.UPCOMING,
    val totalDurationMillis: Long = 0L,
    val elapsedMillis: Long = 0L,
) {
    val progressFraction: Float
        get() = if (totalDurationMillis <= 0L) 0f
                else (elapsedMillis.toFloat() / totalDurationMillis).coerceIn(0f, 1f)

    val isFinished get() = status == ExamStatus.COMPLETED
}

data class ExamUiState(
    val exam: Exam,
    val countdown: CountdownState,
)

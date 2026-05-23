package com.exam.countdown.utils

import com.exam.countdown.model.APP_TIMEZONE
import com.exam.countdown.model.CountdownState
import com.exam.countdown.model.ExamStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object DateTimeUtils {

    val IST: TimeZone = TimeZone.of(APP_TIMEZONE)

    fun computeCountdown(nowMillis: Long, examTimeMillis: Long): CountdownState {
        val diffMillis = examTimeMillis - nowMillis
        return when {
            diffMillis > 0L -> {
                val totalSeconds = diffMillis / 1_000L
                CountdownState(
                    days = totalSeconds / 86_400L,
                    hours = (totalSeconds % 86_400L) / 3_600L,
                    minutes = (totalSeconds % 3_600L) / 60L,
                    seconds = totalSeconds % 60L,
                    status = ExamStatus.UPCOMING,
                    totalDurationMillis = examTimeMillis,
                    elapsedMillis = nowMillis,
                )
            }
            diffMillis > -3_600_000L -> CountdownState(status = ExamStatus.STARTED)
            else -> CountdownState(status = ExamStatus.COMPLETED)
        }
    }

    fun formatExamDateTime(epochMillis: Long): String {
        val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(IST)
        val month = dt.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val hour12 = when { dt.hour == 0 -> 12; dt.hour > 12 -> dt.hour - 12; else -> dt.hour }
        val amPm = if (dt.hour < 12) "AM" else "PM"
        return "${dt.dayOfMonth} $month ${dt.year}, $hour12:${dt.minute.toString().padStart(2,'0')} $amPm"
    }

    fun toEpochMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long =
        LocalDateTime(year, month, day, hour, minute).toInstant(IST).toEpochMilliseconds()

    fun todayInIST(): Triple<Int, Int, Int> {
        val dt = Clock.System.now().toLocalDateTime(IST)
        return Triple(dt.year, dt.monthNumber, dt.dayOfMonth)
    }
}

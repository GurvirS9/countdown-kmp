package com.exam.countdown.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.exam.countdown.model.Exam
import kotlinx.datetime.Clock
import java.util.UUID
import java.util.concurrent.TimeUnit

/** Android implementation using WorkManager for reliable background delivery. */
actual class NotificationScheduler(private val context: Context) {

    actual fun scheduleReminders(exam: Exam) {
        val workManager = WorkManager.getInstance(context)
        val now = Clock.System.now().toEpochMilliseconds()
        val oneDayBefore = exam.examTimeEpochMillis - 86_400_000L
        val oneHourBefore = exam.examTimeEpochMillis - 3_600_000L

        listOf(
            oneDayBefore to "1 day before your exam",
            oneHourBefore to "1 hour to go!",
            exam.examTimeEpochMillis to "Exam time!",
        ).forEachIndexed { i, (triggerAt, message) ->
            val delay = triggerAt - now
            if (delay <= 0L) return@forEachIndexed
            val data = Data.Builder()
                .putString("examName", exam.name)
                .putString("message", message)
                .putLong("examId", exam.id)
                .build()
            val request = OneTimeWorkRequestBuilder<ExamReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("exam_${exam.id}_$i")
                .build()
            workManager.enqueue(request)
        }
    }

    actual fun cancelReminders(examId: Long) {
        val workManager = WorkManager.getInstance(context)
        (0..2).forEach { i -> workManager.cancelAllWorkByTag("exam_${examId}_$i") }
    }
}

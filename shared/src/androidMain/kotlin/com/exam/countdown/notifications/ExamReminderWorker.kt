package com.exam.countdown.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/** WorkManager worker that posts an exam reminder notification. */
class ExamReminderWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val examName = inputData.getString("examName") ?: "Exam"
        val message = inputData.getString("message") ?: "Reminder"
        val examId = inputData.getLong("examId", 0L)

        val channelId = "exam_reminders"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(channelId, "Exam Reminders", NotificationManager.IMPORTANCE_HIGH)
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📚 $examName")
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        manager.notify(examId.toInt(), notification)
        return Result.success()
    }
}

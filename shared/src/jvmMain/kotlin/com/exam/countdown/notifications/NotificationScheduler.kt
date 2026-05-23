package com.exam.countdown.notifications

import com.exam.countdown.model.Exam
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.Toolkit
import java.util.Timer
import java.util.TimerTask
import kotlinx.datetime.Clock

/** Desktop actual — uses java.awt.SystemTray for tray notifications. */
actual class NotificationScheduler {

    private val timer = Timer(/* isDaemon= */ true)

    actual fun scheduleReminders(exam: Exam) {
        if (!SystemTray.isSupported()) return
        val now = Clock.System.now().toEpochMilliseconds()
        listOf(
            exam.examTimeEpochMillis - 86_400_000L to "Tomorrow: ${exam.name}",
            exam.examTimeEpochMillis - 3_600_000L to "1 hour: ${exam.name}",
            exam.examTimeEpochMillis to "${exam.name} — Exam now!",
        ).forEach { (triggerAt, message) ->
            val delay = triggerAt - now
            if (delay <= 0L) return@forEach
            timer.schedule(object : TimerTask() {
                override fun run() = showTrayNotification(exam.name, message)
            }, delay)
        }
    }

    actual fun cancelReminders(examId: Long) {
        // TimerTasks can't be individually cancelled by ID here;
        // a production impl would track tasks in a map by examId.
        timer.purge()
    }

    private fun showTrayNotification(title: String, message: String) {
        runCatching {
            val tray = SystemTray.getSystemTray()
            val image = Toolkit.getDefaultToolkit().createImage(
                ByteArray(0) // minimal 1x1 image placeholder
            )
            val icon = TrayIcon(image, title)
            icon.isImageAutoSize = true
            tray.add(icon)
            icon.displayMessage(title, message, TrayIcon.MessageType.INFO)
            Thread.sleep(5_000)
            tray.remove(icon)
        }
    }
}

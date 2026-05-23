package com.exam.countdown.notifications

import com.exam.countdown.model.Exam

/** JS/Web — no-op placeholder. */
actual class NotificationScheduler {
    actual fun scheduleReminders(exam: Exam) {}
    actual fun cancelReminders(examId: Long) {}
}

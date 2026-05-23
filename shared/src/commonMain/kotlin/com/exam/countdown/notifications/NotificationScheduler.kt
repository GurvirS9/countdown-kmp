package com.exam.countdown.notifications

import com.exam.countdown.model.Exam

/** Platform-specific notification scheduler for exam reminders. */
expect class NotificationScheduler {
    /** Schedule reminders: 1 day before, 1 hour before, and at exam time. */
    fun scheduleReminders(exam: Exam)
    /** Cancel all pending notifications for the given exam. */
    fun cancelReminders(examId: Long)
}

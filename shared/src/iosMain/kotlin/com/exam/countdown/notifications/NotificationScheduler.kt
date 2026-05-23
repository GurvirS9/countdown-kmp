package com.exam.countdown.notifications

import com.exam.countdown.model.Exam

/**
 * iOS actual — integrates with UNUserNotificationCenter via Swift interop.
 * Wire this to the iOS app's AppDelegate using the NotificationSchedulerHelper in iosApp/.
 */
actual class NotificationScheduler {
    actual fun scheduleReminders(exam: Exam) {
        // TODO: Call Swift UNUserNotificationCenter via Kotlin/Native interop
        // Implementation delegated to the iosApp XCode project
    }
    actual fun cancelReminders(examId: Long) {
        // TODO: UNUserNotificationCenter.current().removePendingNotificationRequests(...)
    }
}

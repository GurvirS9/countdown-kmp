package com.exam.countdown.notifications

import com.exam.countdown.model.Exam

/** WasmJS/Web — no-op. Browser Notification API requires a Service Worker setup. */
actual class NotificationScheduler {
    actual fun scheduleReminders(exam: Exam) { /* TODO: Web Push / Service Worker */ }
    actual fun cancelReminders(examId: Long) { /* no-op */ }
}

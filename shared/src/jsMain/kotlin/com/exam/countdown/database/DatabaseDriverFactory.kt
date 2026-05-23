package com.exam.countdown.database

import app.cash.sqldelight.db.SqlDriver

/** JS target — no SQLDelight driver available; will use in-memory repository via DI. */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver = throw UnsupportedOperationException("Use InMemoryExamRepository on JS")
}

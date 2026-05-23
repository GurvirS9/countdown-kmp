package com.exam.countdown.database

import app.cash.sqldelight.db.SqlDriver

/** WasmJS target — no SQLDelight driver; InMemoryRepository is used instead via Koin. */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver = throw UnsupportedOperationException("Use InMemoryExamRepository on WasmJS")
}

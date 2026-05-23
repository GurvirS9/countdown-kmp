package com.exam.countdown.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/** iOS actual: uses NativeSqliteDriver (SQLite.framework). */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(ExamDatabase.Schema, "exam.db")
}

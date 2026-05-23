package com.exam.countdown.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/** Android actual: uses AndroidSqliteDriver backed by a persistent SQLite file. */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(ExamDatabase.Schema, context, "exam.db")
}

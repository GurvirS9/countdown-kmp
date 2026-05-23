package com.exam.countdown.database

import app.cash.sqldelight.db.SqlDriver

/** Platform-specific factory for creating the SQLDelight SqlDriver. */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

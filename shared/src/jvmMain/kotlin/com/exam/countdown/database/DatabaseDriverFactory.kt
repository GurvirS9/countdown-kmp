package com.exam.countdown.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

/** JVM/Desktop actual: uses JdbcSqliteDriver with a local file. */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbPath = File(System.getProperty("user.home"), ".examcountdown/exam.db")
        dbPath.parentFile?.mkdirs()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath.absolutePath}")
        ExamDatabase.Schema.create(driver)
        return driver
    }
}

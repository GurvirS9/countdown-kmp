package com.exam.countdown.di

import com.exam.countdown.database.DatabaseDriverFactory
import com.exam.countdown.database.ExamDatabase
import com.exam.countdown.notifications.NotificationScheduler
import com.exam.countdown.repository.ExamRepository
import com.exam.countdown.repository.SqlDelightExamRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val jvmPlatformModule = module {
    single { DatabaseDriverFactory() }
    single { ExamDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<ExamRepository> { SqlDelightExamRepository(get()) }
    single { NotificationScheduler() }
}

actual fun platformModule(): Module = jvmPlatformModule

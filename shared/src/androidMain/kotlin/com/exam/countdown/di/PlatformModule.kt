package com.exam.countdown.di

import com.exam.countdown.database.DatabaseDriverFactory
import com.exam.countdown.database.ExamDatabase
import com.exam.countdown.notifications.NotificationScheduler
import com.exam.countdown.repository.ExamRepository
import com.exam.countdown.repository.SqlDelightExamRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val androidPlatformModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { ExamDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<ExamRepository> { SqlDelightExamRepository(get()) }
    single { NotificationScheduler(androidContext()) }
}

actual fun platformModule(): Module = androidPlatformModule

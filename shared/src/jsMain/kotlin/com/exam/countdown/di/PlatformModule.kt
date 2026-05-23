package com.exam.countdown.di

import com.exam.countdown.notifications.NotificationScheduler
import com.exam.countdown.repository.ExamRepository
import com.exam.countdown.repository.InMemoryExamRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val jsPlatformModule = module {
    single<ExamRepository> { InMemoryExamRepository() }
    single { NotificationScheduler() }
}

actual fun platformModule(): Module = jsPlatformModule

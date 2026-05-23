package com.exam.countdown

import android.app.Application
import com.exam.countdown.di.androidPlatformModule
import com.exam.countdown.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ExamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ExamApplication)
            modules(androidPlatformModule, viewModelModule)
        }
    }
}

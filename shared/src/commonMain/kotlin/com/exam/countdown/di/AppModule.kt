package com.exam.countdown.di

import com.exam.countdown.viewmodel.AddExamViewModel
import com.exam.countdown.viewmodel.HomeViewModel
import com.exam.countdown.viewmodel.ImportViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Shared Koin module wiring ViewModels. Platform modules provide the repository binding. */
val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddExamViewModel)
    viewModelOf(::ImportViewModel)
}

fun initKoin() {
    startKoin {
        modules(platformModule(), viewModelModule)
    }
}

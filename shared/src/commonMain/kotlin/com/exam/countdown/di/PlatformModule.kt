package com.exam.countdown.di

import org.koin.core.module.Module

/** Returns the platform-specific Koin module. */
expect fun platformModule(): Module

package com.exam.countdown.countdown

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

/**
 * Emits the current epoch millis every second.
 * ViewModels collect this shared ticker to recompute countdowns efficiently —
 * only one coroutine drives all cards on screen.
 */
object CountdownEngine {

    /** A cold Flow that ticks every second, emitting current epoch millis in UTC. */
    val ticker: Flow<Long> = flow {
        while (true) {
            emit(Clock.System.now().toEpochMilliseconds())
            delay(1_000L)
        }
    }
}

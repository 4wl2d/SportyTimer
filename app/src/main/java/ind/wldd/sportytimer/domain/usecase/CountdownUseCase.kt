package ind.wldd.sportytimer.domain.usecase

import android.os.SystemClock
import ind.wldd.sportytimer.domain.model.TimerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun countdownSecondsFlow(
    totalSeconds: Int,
    nowMs: () -> Long = { SystemClock.elapsedRealtime() },
): Flow<TimerState> =
    flow {
        require(totalSeconds >= 0) { "totalSeconds must be >= 0" }

        val endAtMs = nowMs() + totalSeconds.toLong() * 1000L

        var lastEmitted = Int.MIN_VALUE

        while (true) {
            val now = nowMs()
            val remainingMs = (endAtMs - now).coerceAtLeast(0L)
            val remainingSecLong = (remainingMs + 999L) / 1000L
            val remainingSec = remainingSecLong.toInt()

            if (remainingSec != lastEmitted) {
                emit(
                    TimerState(
                        currentValue = remainingSec,
                        isRunning = remainingSec > 0,
                        isFinished = remainingSec == 0
                    )
                )
                lastEmitted = remainingSec
            }

            if (remainingMs == 0L) break

            val nextBoundary = endAtMs - (remainingSecLong - 1L) * 1000L
            val delayMs = (nextBoundary - now).coerceAtLeast(1L)
            delay(delayMs)
        }
    }.flowOn(Dispatchers.Default)

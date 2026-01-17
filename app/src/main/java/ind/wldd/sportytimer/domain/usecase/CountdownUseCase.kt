package ind.wldd.sportytimer.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CountdownUseCase {
    private var updateJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob())

    @OptIn(ObsoleteCoroutinesApi::class)
    fun execute(initialValue: Int): StateFlow<Int> {
        // Cancel any existing update job
        updateJob?.cancel()

        val stateFlow = MutableStateFlow(initialValue)
        val startTime = System.currentTimeMillis()
        val totalDurationMillis = initialValue * 1000L

        updateJob =
            scope.launch {
                // Emit initial value immediately
                var lastEmittedValue = initialValue
                stateFlow.value = initialValue

                ticker(1000).receiveAsFlow().collect {
                    val currentTime = System.currentTimeMillis()
                    val elapsedTime = currentTime - startTime
                    val remainingTime = (totalDurationMillis - elapsedTime).coerceAtLeast(0)
                    val remainingSeconds = (remainingTime / 1000).toInt()

                    // Update if value changed or if we've reached 0
                    if (remainingSeconds != lastEmittedValue || remainingSeconds == 0) {
                        stateFlow.value = remainingSeconds
                        lastEmittedValue = remainingSeconds
                    }

                    if (remainingSeconds <= 0) {
                        cancel()
                    }
                }
            }

        return stateFlow.asStateFlow()
    }

    fun cancel() {
        updateJob?.cancel()
        scope.cancel()
    }
}

package ind.wldd.sportytimer.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CountdownUseCase {
    private var updateJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob())

    fun execute(initialValue: Int): StateFlow<Int> {
        updateJob?.cancel()

        val stateFlow = MutableStateFlow(initialValue)

        updateJob =
            scope.launch {
                flow {
                    var secondsRemaining = initialValue

                    emit(initialValue)

                    while (secondsRemaining > 0) {
                        delay(1000)
                        secondsRemaining--
                        emit(secondsRemaining)
                    }
                }.collect { value ->
                    stateFlow.value = value
                }
            }

        return stateFlow.asStateFlow()
    }

    fun cancel() {
        updateJob?.cancel()
        scope.cancel()
    }
}

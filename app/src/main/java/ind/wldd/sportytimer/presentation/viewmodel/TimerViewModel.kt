package ind.wldd.sportytimer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ind.wldd.sportytimer.domain.model.TimerState
import ind.wldd.sportytimer.domain.usecase.countdownSecondsFlow
import ind.wldd.sportytimer.presentation.model.TimerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startTimer()
    }

    fun startTimer(seconds: Int = 10) {
        val initialTimerState = TimerState(
            currentValue = seconds,
            isRunning = true,
            isFinished = false
        )

        if (initialTimerState.isRunning && _uiState.value.isRunning) return

        countdownJob?.cancel()
        _uiState.value = initialTimerState.toUiState()

        countdownJob = viewModelScope.launch {
            countdownSecondsFlow(seconds).collect { timerState ->
                _uiState.value = timerState.toUiState()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    private fun TimerState.toUiState(): TimerUiState {
        return TimerUiState(
            currentValue = currentValue,
            isRunning = isRunning,
            isFinished = isFinished
        )
    }
}

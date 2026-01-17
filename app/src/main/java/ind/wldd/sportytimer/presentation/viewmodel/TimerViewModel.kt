package ind.wldd.sportytimer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ind.wldd.sportytimer.domain.usecase.CountdownUseCase
import ind.wldd.sportytimer.presentation.model.TimerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    private val countdownUseCase: CountdownUseCase = CountdownUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        if (_uiState.value.isRunning) return

        countdownJob?.cancel()
        _uiState.value = TimerUiState(
            currentValue = 10,
            isRunning = true,
            isFinished = false
        )

        countdownJob = viewModelScope.launch {
            countdownUseCase.execute(10).collect { value ->
                _uiState.value = TimerUiState(
                    currentValue = value,
                    isRunning = value > 0,
                    isFinished = value == 0
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

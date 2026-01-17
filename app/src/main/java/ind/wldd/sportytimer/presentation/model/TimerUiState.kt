package ind.wldd.sportytimer.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class TimerUiState(
    val currentValue: Int = 10,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)

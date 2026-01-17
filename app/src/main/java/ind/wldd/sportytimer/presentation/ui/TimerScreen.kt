package ind.wldd.sportytimer.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ind.wldd.sportytimer.presentation.model.TimerUiState
import ind.wldd.sportytimer.presentation.viewmodel.TimerViewModel

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    TimerContent(
        state = uiState.value,
        modifier = modifier,
    )
}

@Composable
fun TimerContent(
    modifier: Modifier = Modifier,
    state: TimerUiState,
) {
    val displayText =
        remember(state.currentValue) {
            state.currentValue.toString()
        }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

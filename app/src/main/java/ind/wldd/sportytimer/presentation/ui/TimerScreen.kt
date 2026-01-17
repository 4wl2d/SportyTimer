package ind.wldd.sportytimer.presentation.ui

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import ind.wldd.sportytimer.presentation.viewmodel.TimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel = viewModel(),
) {
    val currentValue = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            viewModel.uiState.collect { uiState ->
                currentValue.intValue = uiState.currentValue
            }
        }
    }

    TimerContent(
        currentValueState = currentValue,
        modifier = modifier,
    )
}

@Composable
fun TimerContent(
    modifier: Modifier = Modifier,
    currentValueState: MutableState<Int>,
) {
    val density = LocalDensity.current
    val textStyle = MaterialTheme.typography.displayLarge
    val textColor = MaterialTheme.colorScheme.onBackground

    // Convert Compose text style to Android Paint
    val paint =
        remember(textStyle, textColor, density) {
            Paint().apply {
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                color = textColor.toArgb()
                textSize = with(density) { textStyle.fontSize.toPx() }
                typeface =
                    android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        when (textStyle.fontWeight) {
                            FontWeight.Bold -> android.graphics.Typeface.BOLD
                            FontWeight.Normal -> android.graphics.Typeface.NORMAL
                            else -> android.graphics.Typeface.NORMAL
                        },
                    )
            }
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .drawWithContent {
                    val text = currentValueState.value.toString()

                    val centerX = size.width / 2f
                    val centerY = size.height / 2f

                    drawContent()
                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        centerX,
                        centerY - (paint.descent() + paint.ascent()) / 2f,
                        paint,
                    )
                },
        contentAlignment = Alignment.Center,
    ) {
        // Empty content - text is drawn via drawWithContent
    }
}

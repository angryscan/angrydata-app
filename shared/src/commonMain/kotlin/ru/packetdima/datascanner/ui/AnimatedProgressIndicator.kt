package ru.packetdima.datascanner.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedProgressIndicator(
    indicatorProgress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    color: Color,
    progressAnimDuration: Int = 1500
) {
    var progress by remember { mutableStateOf(0f) }
    val progressAnimation by animateFloatAsState(
        targetValue = indicatorProgress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing)
    )
    LinearProgressIndicator(
        progress = { progressAnimation },
        modifier = modifier,
        color = color,
        trackColor = backgroundColor
    )
    LaunchedEffect(indicatorProgress) {
        progress = indicatorProgress
    }
}
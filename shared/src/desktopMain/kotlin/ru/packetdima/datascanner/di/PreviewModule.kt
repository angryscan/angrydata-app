package ru.packetdima.datascanner.di

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import ru.packetdima.datascanner.ui.theme.AppShapes
import ru.packetdima.datascanner.ui.theme.DarkColors
import ru.packetdima.datascanner.ui.theme.LightColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewModule(
    isDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {

    val colors = if (isDarkTheme) {
        DarkColors
    } else {
        LightColors
    }

    val appRippleConfiguration =
        RippleConfiguration(
            color = if (isDarkTheme) Color.LightGray else Color.DarkGray,
            rippleAlpha = RippleAlpha(
                focusedAlpha = 0.1f,
                hoveredAlpha = 0.1f,
                pressedAlpha = 0.25f,
                draggedAlpha = 0.25f
            ),
        )

    CompositionLocalProvider(LocalRippleConfiguration provides appRippleConfiguration) {
        MaterialTheme(
            colorScheme = colors,
            content = content,
            shapes = AppShapes
        )
    }
}
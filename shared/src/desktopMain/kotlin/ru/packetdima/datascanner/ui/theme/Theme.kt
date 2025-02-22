package ru.packetdima.datascanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.RippleDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.AppSettings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val appSettings = koinInject<AppSettings>()
    val theme by remember { appSettings.theme }
    val colors = if (theme == AppSettings.ThemeType.Dark || (theme == AppSettings.ThemeType.System && isSystemInDarkTheme())) {
        DarkColors
    } else {
        LightColors
    }

    val appRippleConfiguration =
        RippleConfiguration(
            color = MaterialTheme.colorScheme.primary,
            rippleAlpha = RippleDefaults.RippleAlpha
        )

    CompositionLocalProvider(LocalRippleConfiguration provides appRippleConfiguration) {
        MaterialTheme(
            colorScheme = colors,
            content = content,
            shapes = AppShapes
        )
    }
}

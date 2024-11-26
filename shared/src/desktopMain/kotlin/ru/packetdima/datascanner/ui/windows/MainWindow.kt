package ru.packetdima.datascanner.ui.windows

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.ui.UIProperties
import ru.packetdima.datascanner.ui.screens.scanner.MainWindowScreen
import ru.packetdima.datascanner.ui.theme.CustomTheme
import ru.packetdima.datascanner.ui.windows.items.MainWindowTitleBarView
import ru.packetdima.datascanner.ui.windows.items.TitleBar
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.appName
import ru.packetdima.datascanner.resources.icon


@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainWindow(
    isVisible: Boolean,
    setVisibility: (Boolean) -> Unit,
) {
    val theme by remember { Settings.ui.theme }
    val windowState = rememberWindowState(width = 900.dp, height = 675.dp)
    val focusRemember by remember { Settings.focusRequested }

    LaunchedEffect(focusRemember) {
        if (focusRemember) {
            setVisibility(true)
            windowState.isMinimized = false
            Settings.focusRequested.value = false
        }
    }

    Window(
        onCloseRequest = { setVisibility(false) },
        title = stringResource(Res.string.appName),
        state = windowState,
        undecorated = true,
        transparent = true,
        icon = painterResource(Res.drawable.icon),
        visible = isVisible,
        alwaysOnTop = focusRemember
    ) {

        CustomTheme(
            when (theme) {
                UIProperties.ThemeType.System -> isSystemInDarkTheme()
                UIProperties.ThemeType.Dark -> true
                UIProperties.ThemeType.Light -> false
            }
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colors.primary.copy(alpha = 0.5f)), shape = RoundedCornerShape(4.dp))

            ) {
                TitleBar(windowPlacement = windowState.placement) {
                    MainWindowTitleBarView(
                        windowPlacement = windowState.placement,
                        onCloseApp = {
                            setVisibility(false)
                        },
                        onMinimize = {
                            windowState.isMinimized = true
                        },
                        onMaximize = { maximized ->
                            if (maximized) {
                                windowState.placement = WindowPlacement.Maximized
                            } else {
                                windowState.placement = WindowPlacement.Floating
                            }
                        }
                    )
                }
                MainWindowScreen()
            }
        }
    }
}


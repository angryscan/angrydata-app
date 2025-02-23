package ru.packetdima.datascanner.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.appName
import ru.packetdima.datascanner.resources.icon
import ru.packetdima.datascanner.scan.common.ScanPathHelper
import ru.packetdima.datascanner.ui.theme.AppTheme
import ru.packetdima.datascanner.ui.windows.components.MainWindowTitleBar
import ru.packetdima.datascanner.ui.windows.components.SideMenu
import ru.packetdima.datascanner.ui.windows.screens.MainScreen
import java.util.*

@Composable
fun MainWindow(
    onCloseRequest: () -> Unit,
    onHideRequest: () -> Unit,
    isVisible: Boolean
) {
    val windowState = rememberWindowState(width = 1280.dp, height = 720.dp)
    val focusRemember by ScanPathHelper.focusRequested.collectAsState()

    val appSettings = koinInject<AppSettings>()

    val hideOnMinimize by remember { appSettings.hideOnMinimize }

    val appLocale by remember { appSettings.language }
    LaunchedEffect(appLocale) {
        Locale.setDefault(Locale.forLanguageTag(appLocale.locale))
    }

    LaunchedEffect(focusRemember) {
        if (focusRemember) {
            delay(100)
            ScanPathHelper.resetFocus()
        }
    }

    Window(
        onCloseRequest = onCloseRequest,
        title = stringResource(Res.string.appName),
        state = windowState,
        undecorated = true,
        transparent = true,
        icon = painterResource(Res.drawable.icon),
        visible = isVisible,
        alwaysOnTop = focusRemember
    ) {
        AppTheme {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxSize(),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SideMenu()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        MainWindowTitleBar(
                            windowPlacement = windowState.placement,
                            expanded = windowState.placement == WindowPlacement.Maximized,
                            onMinimizeClick = {
                                if(hideOnMinimize)
                                    onHideRequest()
                                else
                                    windowState.isMinimized = true
                            },
                            onExpandClick = {
                                if (windowState.placement == WindowPlacement.Maximized)
                                    windowState.placement = WindowPlacement.Floating
                                else
                                    windowState.placement = WindowPlacement.Maximized
                            },
                            onCloseClick = onCloseRequest
                        )

                        MainScreen()
                    }
                }
            }
        }
    }
}
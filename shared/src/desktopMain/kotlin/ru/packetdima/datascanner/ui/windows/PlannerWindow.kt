package ru.packetdima.datascanner.ui.windows

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.ui.UIProperties
import ru.packetdima.datascanner.ui.screens.planner.PlannerWindowScreen
import ru.packetdima.datascanner.ui.theme.CustomTheme
import ru.packetdima.datascanner.ui.windows.items.PlannerWindowTitleBarView
import ru.packetdima.datascanner.ui.windows.items.TitleBar

@Composable
fun PlannerWindow(isVisible: Boolean, setVisibility: (Boolean) -> Unit) {
    val windowState = rememberWindowState(width = 900.dp, height = 675.dp)
    Window(
        visible = isVisible,
        title = "Planner",
        state = windowState,
        onCloseRequest = { setVisibility(false) },
        undecorated = true,
        transparent = true,
    ){
        CustomTheme(
            when (Settings.ui.theme.value) {
                UIProperties.ThemeType.System -> isSystemInDarkTheme()
                UIProperties.ThemeType.Dark -> true
                UIProperties.ThemeType.Light -> false
            }
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colors.primary), shape = RoundedCornerShape(4.dp))

            ) {
                TitleBar(windowPlacement = windowState.placement){
                    PlannerWindowTitleBarView(
                        windowPlacement = windowState.placement,
                        onCloseApp = {
                            setVisibility(false)
                        },
                        onMinimize = {
                            windowState.isMinimized = true
                        },
                        onMaximize = { maximized ->
                            if(maximized) {
                                windowState.placement = WindowPlacement.Maximized
                            } else {
                                windowState.placement = WindowPlacement.Floating
                            }
                        }
                    )
                }
                MaterialTheme{
                    Column(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(0.dp, 38.dp, 0.dp, 0.dp)
                    ) {
                        PlannerWindowScreen()
                    }
                }
            }
        }
    }
}
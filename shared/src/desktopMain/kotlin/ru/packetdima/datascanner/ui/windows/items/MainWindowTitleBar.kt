package ru.packetdima.datascanner.ui.windows.items

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPlacement
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.ui.custom.AnimatedPopup
import ru.packetdima.datascanner.ui.windows.AppInfoWindow
import ru.packetdima.datascanner.ui.windows.settings.SettingsWindow
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.appName
import kotlin.time.Duration.Companion.seconds


enum class ExpandedItem {
    Settings,
    Info,
    None
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainWindowTitleBarView(
    windowPlacement: WindowPlacement,
    onCloseApp: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: (Boolean) -> Unit,
) {
    var expandedItem by remember { mutableStateOf(ExpandedItem.None) }

    val density = LocalDensity.current

    var showMem by remember { mutableStateOf(false) }

    var heapSize by remember { mutableStateOf(Runtime.getRuntime().totalMemory()) }
    var heapMaxSize by remember { mutableStateOf(Runtime.getRuntime().maxMemory()) }
    var heapFreeSize by remember { mutableStateOf(Runtime.getRuntime().freeMemory()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5.seconds)
            showMem = Settings.ui.propertiesList.contains("heap_memory")
        }
    }

    LaunchedEffect(Unit) {
        while (showMem) {
            delay(1.seconds)
            heapSize = Runtime.getRuntime().totalMemory()
            heapMaxSize = Runtime.getRuntime().maxMemory()
            heapFreeSize = Runtime.getRuntime().freeMemory()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                stringResource(Res.string.appName),
                fontSize = 17.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colors.onSurface
            )
        }
        if (showMem) {
            Column {
                Text(
                    "Max memory : $heapMaxSize",
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    "Total memory: $heapSize",
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    "Free memory: $heapFreeSize",
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        expandedItem = when (expandedItem) {
                            ExpandedItem.Info -> ExpandedItem.None
                            else -> ExpandedItem.Info
                        }
                    },
                    modifier = Modifier.padding(5.dp, 0.dp).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Info, "Info", tint = MaterialTheme.colors.onSurface)
                }
                AnimatedPopup(
                    expanded = expandedItem == ExpandedItem.Info,
                    density = density,
                    onClose = { expandedItem = ExpandedItem.None }
                ) {
                    AppInfoWindow(
                        onCloseClick = { expandedItem = ExpandedItem.None }
                    )
                }
                IconButton(
                    onClick = {
                        expandedItem = when (expandedItem) {
                            ExpandedItem.Settings -> ExpandedItem.None
                            else -> ExpandedItem.Settings
                        }
                    },
                    modifier = Modifier.padding(5.dp, 0.dp).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Settings, "Settings", tint = MaterialTheme.colors.onSurface)
                }
                AnimatedPopup(
                    expanded = expandedItem == ExpandedItem.Settings,
                    density = density,
                    onClose = { expandedItem = ExpandedItem.None }
                ) {
                    SettingsWindow(
                        onCloseClick = {
                            expandedItem = ExpandedItem.None
                        }
                    )
                }
                IconButton(
                    onClick = { onMinimize() },
                    modifier = Modifier.padding(5.dp, 0.dp).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Minimize, "Minimize", tint = MaterialTheme.colors.onSurface)
                }
                IconButton(
                    onClick = { onMaximize(windowPlacement != WindowPlacement.Maximized) },
                    modifier = Modifier.padding(5.dp, 0.dp).size(24.dp)
                ) {
                    Icon(
                        if (windowPlacement == WindowPlacement.Floating) {
                            Icons.Outlined.OpenInFull
                        } else {
                            Icons.Outlined.CloseFullscreen
                        },
                        "Maximize",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
                IconButton(
                    onClick = { onCloseApp() },
                    modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Close, "Close", tint = MaterialTheme.colors.onSurface)
                }
            }

        }
    }
}
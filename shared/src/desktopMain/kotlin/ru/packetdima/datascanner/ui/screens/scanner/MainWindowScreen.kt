package ru.packetdima.datascanner.ui.screens.scanner

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.ui.AnimatedProgressIndicator
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.screenResult
import ru.packetdima.datascanner.resources.screenScanning
import ru.packetdima.datascanner.resources.screenSelectPath

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainWindowScreen() {
    var screenState by remember { mutableStateOf<Screen>(Screen.SelectPathScreen) }
    var pathSelected by remember { mutableStateOf(false) }
    var exported by remember { mutableStateOf(false) }
    var progressSelectScreen by remember { mutableStateOf(0f) }
    var progressScanningScreen by remember { mutableStateOf(0f) }
    var screenNumber by remember { mutableStateOf(0) }

    Crossfade(
        targetState = screenState,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    ) { screen ->
        when (screen) {
            is Screen.SelectPathScreen -> {
                LaunchedEffect(Unit) {
                    if (screenNumber == 2) {
                        progressScanningScreen = 0f
                        delay(1100)
                        screenNumber = 1
                    }
                    progressSelectScreen = 0f
                    delay(1100)
                    screenNumber = 0
                }
                SelectPathScreen(
                    onStart = { screenState = Screen.ScanningScreen(directory = it) },
                    onPathSelectionChanged = { pathSelected = it }
                )
            }
            is Screen.ScanningScreen -> {
                LaunchedEffect(Unit) {
                    progressSelectScreen = 1f
                    progressScanningScreen = 0f
                    delay(1100)
                    screenNumber = 1
                }
                ScanningScreen(
                    directory = screen.directory,
                    onCancel = {
                        screenState = Screen.SelectPathScreen
                        pathSelected = false
                    },
                    onComplete = { filesCounter,
                                   timeSpent ->
                        screenState = Screen.ResultScreen(
                            filesCounter,
                            timeSpent
                        )
                    }
                )
            }
            is Screen.ResultScreen -> {
                LaunchedEffect(Unit) {
                    progressSelectScreen = 1f
                    progressScanningScreen = 1f
                    delay(1100)
                    screenNumber = 2
                }
                ResultScreen(
                    onExported = {
                        exported = true
                    },
                    filesCounter = screen.filesCounter,
                    timeSpent = screen.timeSpent,
                    onComplete = {
                        screenState = Screen.SelectPathScreen
                        pathSelected = false
                    }
                )
            }
        }
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(0.dp, 0.dp, 0.dp, 15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(650.dp).height(24.dp)
        ) {
            IconToggleButton(
                checked = screenState !is Screen.SelectPathScreen && pathSelected,
                onCheckedChange = {
                    if (!it && screenState !is Screen.ScanningScreen) {
                        screenState = Screen.SelectPathScreen
                        pathSelected = false
                    }
                },
                modifier = Modifier.size(24.dp)
                    .testTag("toggle_select_path")
            ) {
                Icon(
                    if (screenNumber == 0 && !pathSelected) Icons.Filled.RadioButtonUnchecked else Icons.Filled.CheckCircle,
                    contentDescription = stringResource(Res.string.screenSelectPath),
                    tint = if (screenNumber == 0 && !pathSelected) MaterialTheme.colors.onBackground else MaterialTheme.colors.primary
                )
            }

            AnimatedProgressIndicator(
                indicatorProgress = progressSelectScreen,
                modifier = Modifier.width(290.dp).height(2.dp)
                    .testTag("progress_select_path"),
                backgroundColor = MaterialTheme.colors.onBackground,
                color = MaterialTheme.colors.primary
            )

            if (screenNumber == 1 && screenState is Screen.ScanningScreen) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.size(22.dp).padding(1.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconToggleButton(
                    checked = screenNumber >= 1,
                    onCheckedChange = { },
                    modifier = Modifier.size(24.dp)
                        .testTag("toggle_scanning")
                ) {
                    Icon(
                        if (screenNumber >= 1 && progressSelectScreen > 0f) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = stringResource(Res.string.screenScanning),
                        tint = if (screenNumber >= 1) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
                    )
                }
            }

            AnimatedProgressIndicator(
                indicatorProgress = progressScanningScreen,
                modifier = Modifier.width(290.dp).height(2.dp)
                    .testTag("progress_scanning"),
                backgroundColor = MaterialTheme.colors.onBackground,
                color = MaterialTheme.colors.primary
            )

            IconToggleButton(
                checked = screenState is Screen.ResultScreen,
                onCheckedChange = { },
                modifier = Modifier.size(24.dp)
                    .testTag("toggle_result")
            ) {
                Icon(
                    if (screenNumber == 2) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = stringResource(Res.string.screenResult),
                    tint = if (screenNumber == 2) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
                )
            }
        }
    }

}
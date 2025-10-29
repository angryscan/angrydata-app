package ru.packetdima.datascanner.ui.windows.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.SettingsScreen_ThreadsCount
import ru.packetdima.datascanner.scan.ScanService

@Composable
fun ThreadCountSettings() {
    val appSettings = koinInject<AppSettings>()
    val scanService = koinInject<ScanService>()

    var sliderPosition by remember { mutableStateOf(appSettings.threadCount.value.toFloat()) }
    var threadCount by remember { appSettings.threadCount }
    val maxThreads = Runtime.getRuntime().availableProcessors()

    SettingsRow(title = stringResource(Res.string.SettingsScreen_ThreadsCount)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 1f..maxThreads.toFloat(),
                steps = maxThreads - 2,
                onValueChangeFinished = {
                    threadCount = sliderPosition.toInt()
                    appSettings.save()
                    scanService.setThreadsCount()
                },
                modifier = Modifier
                    .sizeIn(maxWidth = 600.dp)
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraSmall
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sliderPosition.toInt().toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}


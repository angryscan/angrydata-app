package ru.packetdima.datascanner.ui.windows.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
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
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.store.ContextMenu
import ru.packetdima.datascanner.ui.icons.icon
import ru.packetdima.datascanner.ui.strings.composableName

@Composable
fun SettingsScreen() {
    val appSettings = koinInject<AppSettings>()
    val scanService = koinInject<ScanService>()

    var sliderPosition by remember { mutableStateOf(appSettings.threadCount.value.toFloat()) }
    var threadCount by remember { appSettings.threadCount }
    val maxThreads = Runtime.getRuntime().availableProcessors()

    var contextMenuEnabled by remember { mutableStateOf(ContextMenu.enabled) }

    var language by remember { appSettings.language }

    var theme by remember { appSettings.theme }

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .width(760.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(6.dp)
        ) {
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
            SettingsRow(title = stringResource(Res.string.SettingsScreen_ContextMenu)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(Res.string.SettingsScreen_ContextMenuExplorer))

                    Switch(
                        checked = contextMenuEnabled,
                        onCheckedChange = {
                            contextMenuEnabled = it
                            ContextMenu.enabled = it
                        }
                    )
                }
            }
            SettingsRow(title = stringResource(Res.string.SettingsScreen_Language)) {
                val rows = AppSettings.LanguageType.entries.size / 3 + if (AppSettings.LanguageType.entries.size % 3 > 0) 1 else 0

                val height = (34 * rows + (6 * (rows - 1))).dp

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .height(height)
                        .fillMaxWidth()
                ) {
                    items(AppSettings.LanguageType.entries) { lang ->
                        Box(
                            modifier = Modifier
                                .size(width = 150.dp, height = 34.dp)
                                .clip(
                                    MaterialTheme.shapes.large
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.large
                                )
                                .background(
                                    color = if(lang == language) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                )
                                .clickable(
                                    enabled = lang != language,
                                    onClick = {
                                        language = lang
                                        appSettings.save()
                                    }
                                )
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.text,
                                fontSize = 14.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
            SettingsRow(title = stringResource(Res.string.SettingsScreen_Theme)) {
                val rows = AppSettings.ThemeType.entries.size / 3 + if (AppSettings.ThemeType.entries.size % 3 > 0) 1 else 0

                val height = (34 * rows + (6 * (rows - 1))).dp

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .height(height)
                        .fillMaxWidth()
                ) {
                    items(AppSettings.ThemeType.entries) { th ->
                        Box(
                            modifier = Modifier
                                .size(width = 150.dp, height = 34.dp)
                                .clip(
                                    MaterialTheme.shapes.large
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.large
                                )
                                .background(
                                    color = if(th == theme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                )
                                .clickable(
                                    enabled = th != theme,
                                    onClick = {
                                        theme = th
                                        appSettings.save()
                                    }
                                )
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                Text(
                                    text = th.composableName(),
                                    fontSize = 14.sp,
                                    lineHeight = 14.sp
                                )
                                Icon(
                                    painter = th.icon(),
                                    contentDescription = null
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}
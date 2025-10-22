package ru.packetdima.datascanner.ui.windows.screens.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.rememberDialogState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.store.ContextMenu
import ru.packetdima.datascanner.ui.dialogs.DescriptionDialog
import ru.packetdima.datascanner.ui.dialogs.LicenseDialog
import ru.packetdima.datascanner.ui.icons.icon
import ru.packetdima.datascanner.ui.strings.composableName
import java.awt.Desktop

@Composable
fun SettingsScreen() {
    val appSettings = koinInject<AppSettings>()
    val scanService = koinInject<ScanService>()

    var sliderPosition by remember { mutableStateOf(appSettings.threadCount.value.toFloat()) }
    var threadCount by remember { appSettings.threadCount }
    val maxThreads = Runtime.getRuntime().availableProcessors()

    var contextMenuEnabled by remember { mutableStateOf(ContextMenu.enabled) }
    var debugModeEnabled by remember { appSettings.debugMode }

    var language by remember { appSettings.language }

    var theme by remember { appSettings.theme }

    
    var showDescriptionDialog by remember { mutableStateOf(false) }
    val descriptionDialogState = rememberDialogState(width = 600.dp, height = 450.dp)
    
    var showLicenseDialog by remember { mutableStateOf(false) }
    val licenseDialogState = rememberDialogState(width = 600.dp, height = 600.dp)

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
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

                if(ContextMenu.supported()) {
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
                }

                SettingsRow(title = stringResource(Res.string.SettingsScreen_Language)) {
                    val rows =
                        AppSettings.LanguageType.entries.size / 3 + if (AppSettings.LanguageType.entries.size % 3 > 0) 1 else 0

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
                                        color = if (lang == language) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
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
                    val rows =
                        AppSettings.ThemeType.entries.size / 3 + if (AppSettings.ThemeType.entries.size % 3 > 0) 1 else 0

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
                                        color = if (th == theme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
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
                SettingsRow(
                    title = stringResource(Res.string.SettingsScreen_Logging)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(Res.string.ScanSettings_DebugMode))

                            Switch(
                                checked = debugModeEnabled,
                                onCheckedChange = {
                                    debugModeEnabled = it
                                    appSettings.save()
                                }
                            )
                        }

                        OutlinedButton(
                            modifier = Modifier
                                .size(width = 150.dp, height = 34.dp),
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                            onClick = {
                                Desktop.getDesktop().open(AppFiles.LoggingDir.toFile())
                            },
                            colors = ButtonDefaults.outlinedButtonColors().copy(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(Res.string.SettingsScreen_OpenFolder),
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                SettingsRow(title = stringResource(Res.string.SideMenu_AboutPage)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .size(width = 140.dp, height = 40.dp),
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                            onClick = { showDescriptionDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors().copy(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(Res.string.About_Description),
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                            )
                        }
                        
                        OutlinedButton(
                            modifier = Modifier
                                .size(width = 120.dp, height = 40.dp),
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                            onClick = { showLicenseDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors().copy(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(Res.string.About_License),
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                            )
                        }
                    }
                }
            }
        }
        
        
        if (showDescriptionDialog) {
            DescriptionDialog(
                onCloseRequest = { showDescriptionDialog = false },
                dialogState = descriptionDialogState
            )
        }
        
        if (showLicenseDialog) {
            LicenseDialog(
                onCloseRequest = { showLicenseDialog = false },
                dialogState = licenseDialogState
            )
        }
    }
}
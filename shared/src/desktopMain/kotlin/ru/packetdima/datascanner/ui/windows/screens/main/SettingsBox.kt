package ru.packetdima.datascanner.ui.windows.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Transition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.ScanSettings_FastScan
import ru.packetdima.datascanner.resources.ScanSettings_FileExtensions
import ru.packetdima.datascanner.resources.ScanSettings_SelectAll
import ru.packetdima.datascanner.scan.common.FileType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBox(transition: Transition<Boolean>) {
    val scanSettings = koinInject<ScanSettings>()

    var fastScan by remember { scanSettings.fastScan }

    var fileExtensionsExpanded by remember { mutableStateOf(false) }
    val fileExtensionIconRotation = remember { Animatable(90f) }

    var extensions = remember { scanSettings.extensions }

    LaunchedEffect(fileExtensionsExpanded) {
        fileExtensionIconRotation.animateTo(if (fileExtensionsExpanded) 90f else 270f)
    }

    LaunchedEffect(fastScan) {
        scanSettings.save()
    }

    AnimatedVisibility(
        transition.currentState,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .height(400.dp)
                .padding(6.dp)
                .fillMaxWidth()
        ) {


            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Fast scan checkbox
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = fastScan,
                        onCheckedChange = { fastScan = it }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = stringResource(Res.string.ScanSettings_FastScan),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                fastScan = !fastScan
                            }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ScanSettings_FastScan),
                            contentDescription = null
                        )
                    }
                }

                // File extensions selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .height(34.dp)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                fileExtensionsExpanded = !fileExtensionsExpanded
                            },
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.ScanSettings_FileExtensions),
                                fontSize = 16.sp
                            )
                            Icon(
                                imageVector = Icons.Outlined.ArrowBackIosNew,
                                contentDescription = null,
                                modifier = Modifier
                                    .rotate(fileExtensionIconRotation.value)
                            )
                        }
                    }
                    AnimatedVisibility(fileExtensionsExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = extensions.containsAll(FileType.entries),
                                    onCheckedChange = { checked ->
                                        extensions = if(checked) {
                                            FileType.entries.toMutableList()
                                        } else {
                                            mutableListOf()
                                        }
                                    }
                                )
                                CompositionLocalProvider(LocalRippleConfiguration provides null) {
                                    Text(
                                        text = stringResource(Res.string.ScanSettings_SelectAll),
                                        fontSize = 14.sp,
                                        modifier = Modifier.clickable {
                                            fastScan = !fastScan
                                        }
                                    )
                                }
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    modifier = Modifier
                                        .height(200.dp)
                                        .fillMaxWidth()
                                ) {
                                    items(FileType.entries) { fileType ->
                                        Checkbox(
                                            checked = extensions.contains(fileType),
                                            onCheckedChange = { checked ->
                                                extensions = if(checked)
                                                    (extensions + fileType).toMutableList()
                                                else
                                                    (extensions - fileType).toMutableList()
                                            }
                                        )
                                        CompositionLocalProvider(LocalRippleConfiguration provides null) {
                                            Text(
                                                text = fileType.name,
                                                fontSize = 14.sp,
                                                modifier = Modifier.clickable {
                                                    fastScan = !fastScan
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
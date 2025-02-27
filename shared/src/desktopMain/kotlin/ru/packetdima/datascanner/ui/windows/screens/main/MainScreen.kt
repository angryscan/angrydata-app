package ru.packetdima.datascanner.ui.windows.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.resources.MainScreen_ScanCurrentState
import ru.packetdima.datascanner.resources.MainScreen_ScanStartButton
import ru.packetdima.datascanner.resources.MainScreen_SelectPathPlaceholder
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.scan.common.ScanPathHelper
import java.io.File
import javax.swing.JFileChooser


@Composable
fun MainScreen() {

    val scanService = koinInject<ScanService>()

    val helperPath = ScanPathHelper.path.collectAsState()

    var path by remember { mutableStateOf(helperPath.value) }

    var settingExpanded by remember { mutableStateOf(false) }

    var scanStateExpanded by remember { mutableStateOf(false) }

    val settingsButtonTransition = updateTransition(settingExpanded)

    val settingsBoxTransition = updateTransition(settingExpanded)

    var selectPathError by remember { mutableStateOf(false) }
    var scanNotCorrectPath by remember { mutableStateOf(false) }

    val scanStateIconRotation = remember { Animatable(270f) }

    LaunchedEffect(scanStateExpanded) {
        scanStateIconRotation.animateTo(if (scanStateExpanded) 270f else 90f)
    }

    LaunchedEffect(scanNotCorrectPath) {
        if (scanNotCorrectPath) {
            selectPathError = true
            delay(200)
            selectPathError = false
            delay(400)
            selectPathError = true
            delay(200)
            selectPathError = false
            delay(400)
            selectPathError = true
            delay(200)
            selectPathError = false
            scanNotCorrectPath = false
        }
    }

    LaunchedEffect(helperPath) {
        if (helperPath.value.isNotEmpty()) {
            path = helperPath.value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .height(80.dp)
                    .width(700.dp),
                value = path,
                onValueChange = { path = it },
                placeholder = { Text(text = stringResource(Res.string.MainScreen_SelectPathPlaceholder)) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                isError = selectPathError,
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .width(64.dp)
                            .size(48.dp)
                            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize(),
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    }
                },
                trailingIcon = {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colorScheme.onBackground)
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable {
                                    val f = JFileChooser()
                                    f.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
                                    f.isMultiSelectionEnabled = false
                                    if (f.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                        path = f.selectedFile.absolutePath
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.background
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Button(
                        onClick = {
                            if(File(path).exists()) {
                                /*TODO*/
                            } else {
                                scanNotCorrectPath = true
                            }
                        },
                        modifier = Modifier
                            .width(268.dp)
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium.copy(
                            topEnd = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.MainScreen_ScanStartButton),
                            fontSize = 24.sp
                        )
                    }
                    SettingsButton(
                        transition = settingsButtonTransition,
                        onClick = {
                            if(!settingExpanded) {
                                scanStateExpanded = false
                                settingExpanded = true
                            } else {
                                settingExpanded = false
                            }
                        }
                    )
                }
                SettingsBox(
                    transition = settingsBoxTransition
                )
            }

        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(700.dp)
                    .height(63.dp)
                    .clip(MaterialTheme.shapes.medium.copy(
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    ))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        if(settingExpanded) {
                            settingExpanded = false
                        }
                        scanStateExpanded = !scanStateExpanded
                    }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(
                            Res.string.MainScreen_ScanCurrentState,
                            scanService.tasks.tasks.value.filter {
                                it.state.value == TaskState.SCANNING ||
                                it.state.value == TaskState.SEARCHING ||
                                it.state.value == TaskState.PENDING
                            }.size
                        ),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(scanStateIconRotation.value)
                    )
                }
            }
            AnimatedVisibility(
                visible = scanStateExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxSize()
                ) {

                }
            }
        }

    }
}


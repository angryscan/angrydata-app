package ru.packetdima.datascanner.ui.windows.screens.main

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
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.resources.MainScreen_ScanStartButton
import ru.packetdima.datascanner.resources.MainScreen_SelectPathPlaceholder
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.scan.common.ScanPathHelper
import ru.packetdima.datascanner.ui.windows.screens.main.settings.SettingsBox
import ru.packetdima.datascanner.ui.windows.screens.main.settings.SettingsButton
import ru.packetdima.datascanner.ui.windows.screens.main.tasks.MainScreenTasks
import java.io.File
import javax.swing.JFileChooser


@Composable
fun MainScreen() {

    val scanService = koinInject<ScanService>()

    val scanSettings = koinInject<ScanSettings>()

    val helperPath = ScanPathHelper.path.collectAsState()

    var path by remember { mutableStateOf(helperPath.value) }

    var settingsExpanded by remember { mutableStateOf(false) }

    var scanStateExpanded by remember { mutableStateOf(false) }

    val settingsButtonTransition = updateTransition(settingsExpanded)

    val settingsBoxTransition = updateTransition(settingsExpanded)

    var selectPathError by remember { mutableStateOf(false) }
    var scanNotCorrectPath by remember { mutableStateOf(false) }


    val coroutineScope = rememberCoroutineScope()

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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {


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
                                if (File(path).exists()) {
                                    coroutineScope.launch {
                                        val task = scanService.createTask(
                                            path = path,
                                            extensions = scanSettings.extensions,
                                            detectFunctions = scanSettings.detectFunctions + scanSettings.userSignatures,
                                            fastScan = scanSettings.fastScan.value
                                        )
                                        scanService.startTask(task)
                                        if (!scanStateExpanded) {
                                            settingsExpanded = false
                                            scanStateExpanded = true
                                        }
                                    }
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
                                if (!settingsExpanded) {
                                    scanStateExpanded = false
                                    settingsExpanded = true
                                } else {
                                    settingsExpanded = false
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

            MainScreenTasks(
                expanded = scanStateExpanded,
                onExpandedClick = {
                    if (!scanStateExpanded) {
                        settingsExpanded = false
                    }
                    scanStateExpanded = !scanStateExpanded
                }
            )
        }
    }
}


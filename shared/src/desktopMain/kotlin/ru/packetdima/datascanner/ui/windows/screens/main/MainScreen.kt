package ru.packetdima.datascanner.ui.windows.screens.main

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.scan.common.ScanPathHelper
import ru.packetdima.datascanner.scan.common.files.FileType
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun
import ru.packetdima.datascanner.ui.windows.screens.main.settings.SettingsBox
import ru.packetdima.datascanner.ui.windows.screens.main.settings.SettingsButton
import ru.packetdima.datascanner.ui.windows.screens.main.tasks.MainScreenTasks
import java.io.File


@Composable
fun MainScreen() {

    val scanService = koinInject<ScanService>()

    val scanSettings = koinInject<ScanSettings>()

    val helperPath by ScanPathHelper.path.collectAsState()
    val focusRequested by ScanPathHelper.focusRequested.collectAsState()

    var path by remember { mutableStateOf(helperPath) }

    var settingsExpanded by remember { mutableStateOf(false) }

    var scanStateExpanded by remember { mutableStateOf(false) }

    val settingsButtonTransition = updateTransition(settingsExpanded)

    val settingsBoxTransition = updateTransition(settingsExpanded)

    var selectPathError by remember { mutableStateOf(false) }
    var scanNotCorrectPath by remember { mutableStateOf(false) }

    var selectionTypeChooserExpanded by remember { mutableStateOf(false) }

    var selectionTypeFolder by remember { mutableStateOf(true) }


    val coroutineScope = rememberCoroutineScope()

    val filePicker = rememberFilePickerLauncher(
        type = FileKitType.File(),
        mode = FileKitMode.Multiple(),
        title = "Select Directory",
    ) { result ->
        if (result != null) {
            path = result.joinToString(";")
        }

    }

    val folderPicker = rememberDirectoryPickerLauncher { dir ->
        if (dir != null) {
            path = dir.path
        }
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
        if (helperPath.isNotEmpty()) {
            path = helperPath
            if (focusRequested)
                ScanPathHelper.resetFocus()
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
                                    .clip(
                                        MaterialTheme.shapes.large.copy(
                                            topEnd = CornerSize(0.dp),
                                            bottomEnd = CornerSize(0.dp)
                                        )
                                    )
                                    .background(MaterialTheme.colorScheme.onBackground)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable {
                                        if (selectionTypeFolder)
                                            folderPicker.launch()
                                        else
                                            filePicker.launch()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector =
                                        if (selectionTypeFolder)
                                            Icons.Outlined.Folder
                                        else
                                            Icons.Outlined.FileOpen,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.background
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(56.dp)
                                    .width(28.dp)
                                    .clip(
                                        MaterialTheme.shapes.large.copy(
                                            topStart = CornerSize(0.dp),
                                            bottomStart = CornerSize(0.dp)
                                        )
                                    )
                                    .background(MaterialTheme.colorScheme.onBackground)
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .clickable {
                                        selectionTypeChooserExpanded = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp),
                                    tint = MaterialTheme.colorScheme.background
                                )
                            }
                            DropdownMenu(
                                expanded = selectionTypeChooserExpanded,
                                onDismissRequest = {
                                    selectionTypeChooserExpanded = false
                                }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        selectionTypeFolder = true
                                        selectionTypeChooserExpanded = false
                                    },
                                    text = { Text(text = stringResource(Res.string.MainScreen_SelectTypeFolder)) }
                                )
                                DropdownMenuItem(
                                    onClick = {
                                        selectionTypeFolder = false
                                        selectionTypeChooserExpanded = false
                                    },
                                    text = { Text(text = stringResource(Res.string.MainScreen_SelectTypeFile)) }
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

                                if (path
                                        .split(";").map {
                                            File(it).exists()
                                        }
                                        .all { it }
                                ) {
                                    coroutineScope.launch {
                                        val extensions = scanSettings.extensions
                                        if (scanSettings.detectCode.value)
                                            extensions.add(FileType.CODE)
                                        if (scanSettings.detectCert.value)
                                            extensions.add(FileType.CERT)

                                        val detectFunctions =
                                            (scanSettings.detectFunctions + scanSettings.userSignatures)
                                                .toMutableList()
                                        if (scanSettings.detectCert.value)
                                            detectFunctions.add(CertDetectFun)
                                        if (scanSettings.detectCode.value)
                                            detectFunctions.add(CodeDetectFun)

                                        val task = scanService.createTask(
                                            path = path,
                                            extensions = scanSettings.extensions,
                                            detectFunctions = detectFunctions,
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


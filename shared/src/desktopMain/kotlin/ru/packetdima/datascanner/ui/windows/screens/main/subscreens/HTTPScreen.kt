package ru.packetdima.datascanner.ui.windows.screens.main.subscreens

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import ru.packetdima.datascanner.scan.common.connectors.ConnectorHTTP
import ru.packetdima.datascanner.scan.common.files.FileType
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun
import ru.packetdima.datascanner.scan.functions.RKNDomainDetectFun
import ru.packetdima.datascanner.ui.windows.components.RadioButtonNavigation
import ru.packetdima.datascanner.ui.windows.screens.main.settings.SettingsBox
import ru.packetdima.datascanner.ui.windows.screens.main.settings.SettingsButton

@Composable
fun HTTPScreen(
    navController: androidx.navigation.NavController,
    settingsExpanded: Boolean,
    expandSettings: () -> Unit,
    hideSettings: () -> Unit,
    expandScanState: (Int) -> Unit
) {
    val scanService = koinInject<ScanService>()

    val scanSettings = koinInject<ScanSettings>()

    val helperPath by ScanPathHelper.path.collectAsState()
    var path by remember { mutableStateOf(helperPath) }

    val settingsButtonTransition = updateTransition(settingsExpanded)

    val settingsBoxTransition = updateTransition(settingsExpanded)

    var scanNotCorrectPath by remember { mutableStateOf(false) }

    var selectPathError by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = if (settingsExpanded) 0.dp else 150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .height(80.dp)
                .width(700.dp),
            value = path,
            onValueChange = {
                path = it
                    .split("\\s".toRegex())
                    .filter { url -> url.trim().isNotEmpty() }
                    .joinToString(";")
            },
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
        )

        Box(
            modifier = Modifier
                .width(700.dp)
                .padding(vertical = 0.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            RadioButtonNavigation(
                navController = navController
            )
        }

        Row {
                Button(
                    onClick = {

                        if (
                            path.split(";").all {
                                it.startsWith("http://") ||
                                        it.startsWith("https://")
                            }
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
                                if(scanSettings.detectBlockedDomains.value)
                                    detectFunctions.add(RKNDomainDetectFun)

                                val task = scanService.createTask(
                                    path = path,
                                    extensions = scanSettings.extensions,
                                    detectFunctions = detectFunctions,
                                    fastScan = scanSettings.fastScan.value,
                                    connector = ConnectorHTTP()
                                )
                                scanService.startTask(task)
                                task.id.value?.let { taskId ->
                                    expandScanState(taskId)
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
                            expandSettings()
                        } else {
                            hideSettings()
                        }
                    }
                )
            }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            SettingsBox(
                transition = settingsBoxTransition
            )
        }
    }
}
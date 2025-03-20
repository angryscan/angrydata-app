package ru.packetdima.datascanner.ui.windows.screens.scans

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.scan.TaskFilesViewModel
import ru.packetdima.datascanner.scan.common.ResultWriter
import ru.packetdima.datascanner.scan.common.createDialogSettings
import ru.packetdima.datascanner.ui.dialogs.DesktopAlertDialog
import ru.packetdima.datascanner.ui.extensions.color
import ru.packetdima.datascanner.ui.extensions.icon
import ru.packetdima.datascanner.ui.windows.screens.scans.components.AttributeCard
import ru.packetdima.datascanner.ui.windows.screens.scans.components.ResultTable
import ru.packetdima.datascanner.ui.windows.screens.scans.components.SortColumn
import ru.packetdima.datascanner.ui.windows.screens.scans.components.comparator

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    taskId: Int,
    onCloseClick: () -> Unit
) {
    val scanService = koinInject<ScanService>()
    val appSettings = koinInject<AppSettings>()
    val task = scanService.tasks.tasks.value.first { it.id.value == taskId }

    val taskFilesViewModel = koinViewModel<TaskFilesViewModel> { parametersOf(task.dbTask) }
    val taskFiles by taskFilesViewModel.taskFiles.collectAsState()

    val clipboardManager = LocalClipboardManager.current

    val coroutineScope = rememberCoroutineScope()

    val state by task.state.collectAsState()
    val path by task.path.collectAsState()
    val fastScan by task.fastScan.collectAsState()
    val startedAt by task.startedAt.collectAsState()
    val finishedAt by task.finishedAt.collectAsState()

    val scanned by task.scannedFiles.collectAsState()
    val skipped by task.skippedFiles.collectAsState()
    val selectedFiles by task.selectedFiles.collectAsState()
    val foundFiles by task.foundFiles.collectAsState()
    val totalFiles by task.totalFiles.collectAsState()

    val foundAttributes by task.foundAttributes.collectAsState()

    val progress = if (selectedFiles > 0) 100 * (scanned + skipped) / selectedFiles else 0

    val animatedProgress by
    animateFloatAsState(
        targetValue = if (selectedFiles > 0) (scanned + skipped).toFloat() / selectedFiles else 0f,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    val folderSize = task.dbTask.size ?: ""

    val dateFormat = LocalDateTime.Format {
        dayOfMonth()
        char('.')
        monthNumber()
        char('.')
        year()
        char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()
    }

    val fileDateFormat = LocalDateTime.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
        char('_')
        hour()
        char('-')
        minute()
        char('-')
        second()
    }

    val dialogSettings = createDialogSettings()

    var reportExtension by remember { appSettings.reportSaveExtension }
    var reportExtensionChooserExpanded by remember { mutableStateOf(false) }
    var errorDialogVisible by remember { mutableStateOf(false) }

    val saveLauncher = rememberFileSaverLauncher(
        dialogSettings = dialogSettings
    ) { file ->
        if (file != null) {
            coroutineScope.launch {
                ResultWriter.saveResult(
                    file.path,
                    taskFiles.sortedWith(
                        SortColumn.Score.comparator()
                    ),
                    onSaveError = {
                        errorDialogVisible = true
                    }
                )
            }
        }

    }

    if (errorDialogVisible) {
        DesktopAlertDialog(
            onCloseRequest = {
                errorDialogVisible = false
            },
            title = stringResource(Res.string.FileSave_ErrorTitle),
            message = stringResource(Res.string.FileSave_ErrorText)
        )
    }

    val shapes = MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .clip(shape = shapes)
            .border(
                shape = shapes,
                color = state.color(),
                width = 1.dp
            )
            .padding(
                start = 15.dp,
                top = 15.dp,
                end = 15.dp
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        IconButton(
                            onClick = onCloseClick
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBackIosNew,
                                contentDescription = null
                            )
                        }

                        Text(
                            text = path,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            letterSpacing = 0.1.sp
                        )

                        Icon(
                            imageVector = Icons.Outlined.CopyAll,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .clickable {
                                    clipboardManager.setText(
                                        annotatedString = AnnotatedString(path)
                                    )
                                }
                        )

                        if (fastScan) {
                            Icon(
                                imageVector = Icons.Outlined.RocketLaunch,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Icon(
                            imageVector = state.icon(),
                            contentDescription = null,
                            tint = state.color()
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(
                            visible = state == TaskState.COMPLETED,
                        ) {
                            Row {


                                Box(
                                    modifier = Modifier
                                        .size(height = 40.dp, width = 90.dp)
                                        .clip(
                                            MaterialTheme.shapes.medium.copy(
                                                bottomEnd = CornerSize(0.dp),
                                                topEnd = CornerSize(0.dp)
                                            )
                                        )
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                        .clickable {
                                            saveLauncher.launch(
                                                suggestedName = "BDS_${fileDateFormat.format(finishedAt!!)}",
                                                extension = reportExtension.extension,
                                                directory = PlatformFile(AppFiles.UserDirPath)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row {
                                        Icon(
                                            imageVector = Icons.Outlined.Download,
                                            contentDescription = null
                                        )
                                        Text(
                                            text = reportExtension.name,
                                            modifier = Modifier
                                                .padding(start = 5.dp)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(
                                            MaterialTheme.shapes.medium.copy(
                                                bottomStart = CornerSize(0.dp),
                                                topStart = CornerSize(0.dp)
                                            )
                                        )
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                        .clickable {
                                            reportExtensionChooserExpanded = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp)
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                .clickable {
                                    onCloseClick()
                                    coroutineScope.launch {
                                        scanService.deleteTask(task)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                            DropdownMenu(
                                expanded = reportExtensionChooserExpanded,
                                onDismissRequest = {
                                    reportExtensionChooserExpanded = false
                                }
                            ) {
                                ResultWriter.FileExtensions.entries.forEach {
                                    DropdownMenuItem(
                                        onClick = {
                                            reportExtension = it
                                            reportExtensionChooserExpanded = false
                                            appSettings.save()
                                        },
                                        text = { Text(text = it.name) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state != TaskState.COMPLETED) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            .clickable {
                                when (state) {
                                    TaskState.SEARCHING, TaskState.SCANNING, TaskState.LOADING, TaskState.PENDING ->
                                        scanService.stopTask(task)

                                    TaskState.STOPPED -> scanService.resumeTask(task)
                                    else -> scanService.rescanTask(task)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (state) {
                                TaskState.SEARCHING, TaskState.SCANNING, TaskState.LOADING, TaskState.PENDING -> Icons.Outlined.Pause
                                TaskState.STOPPED -> Icons.Outlined.PlayArrow
                                else -> Icons.Outlined.RestartAlt
                            },
                            contentDescription = null
                        )
                    }
                    LinearProgressIndicator(
                        progress = {
                            animatedProgress
                        },
                        color = state.color(),
                        modifier = Modifier.width(600.dp),
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    )
                }

            }


            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.Task_TotalFiles),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = totalFiles.toString(),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                    )
                }

                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.Task_SelectedFiles),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = selectedFiles.toString(),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                    )
                }

                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.Task_FoundFiles),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = foundFiles.toString(),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                    )
                }

                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.Task_FolderSize),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = folderSize,
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                    )
                }
                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = stringResource(
                            resource = Res.string.Task_StartedAt,
                            startedAt?.let {
                                dateFormat.format(it)
                            } ?: ""
                        ),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp
                    )

                    if (finishedAt != null && state == TaskState.COMPLETED) {
                        Text(
                            text = stringResource(
                                resource = Res.string.Task_FinishedAt,
                                finishedAt?.let {
                                    dateFormat.format(it)
                                } ?: ""
                            ),
                            fontSize = 14.sp,
                            letterSpacing = 0.1.sp
                        )
                    } else {
                        Text(
                            text = stringResource(
                                resource = Res.string.Task_Progress,
                                progress
                            ),
                            fontSize = 14.sp,
                            letterSpacing = 0.1.sp
                        )
                    }
                }
            }

            if (foundAttributes.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.Task_FoundAttributes),
                        fontSize = 14.sp,
                        letterSpacing = 0.1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        foundAttributes.forEach { attr ->
                            AttributeCard(attr)
                        }
                    }
                }
            }

            ResultTable(
                taskFilesViewModel = taskFilesViewModel,
                task = task
            )
        }
    }
}
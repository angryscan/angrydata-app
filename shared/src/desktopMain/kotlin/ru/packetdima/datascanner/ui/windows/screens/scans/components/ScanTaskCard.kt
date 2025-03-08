package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.TaskEntityViewModel
import ru.packetdima.datascanner.ui.extensions.color
import ru.packetdima.datascanner.ui.extensions.icon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScanTaskCard(
    taskEntity: TaskEntityViewModel,
    onClick: () -> Unit
) {
    val state by taskEntity.state.collectAsState()
    val fastScan by taskEntity.fastScan.collectAsState()
    val path by taskEntity.path.collectAsState()
    val startedAt by taskEntity.startedAt.collectAsState()
    val finishedAt by taskEntity.finishedAt.collectAsState()

    val scanned by taskEntity.scannedFiles.collectAsState()
    val skipped by taskEntity.skippedFiles.collectAsState()
    val selectedFiles by taskEntity.selectedFiles.collectAsState()
    val foundFiles by taskEntity.foundFiles.collectAsState()
    val totalFiles by taskEntity.totalFiles.collectAsState()

    val foundAttributes by taskEntity.foundAttributes.collectAsState()

    val progress = if (selectedFiles > 0) 100 * (scanned + skipped) / selectedFiles else 0

    val folderSize = taskEntity.dbTask.size ?: ""

    val dateFormat = LocalDateTime.Format {
        year()
        char('.')
        monthNumber()
        char('.')
        dayOfMonth()
        char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = state.color(),
                shape = MaterialTheme.shapes.medium
            )
            .clickable(
                onClick = onClick
            )
            .padding(14.dp),

        ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
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

                    Text(
                        text = path,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                        letterSpacing = 0.1.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
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
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp)
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        foundAttributes.forEach { attr ->
                            AttributeCard(attr)
                        }
                    }
                }
            }
        }
    }
}
package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.ui.extensions.icon

@Composable
fun ScanFilterChipBox(
    active: Boolean,
    paused: Boolean,
    error: Boolean,
    completed: Boolean,
    onActiveClick: () -> Unit,
    onPausedClick: () -> Unit,
    onErrorClick: () -> Unit,
    onCompletedClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ScanFilterChip(
            text = stringResource(Res.string.TaskStateChipFilter_Active),
            selected = active,
            onClick = onActiveClick,
            icon = TaskState.SCANNING.icon()
        )
        ScanFilterChip(
            text = stringResource(Res.string.TaskStateChipFilter_Paused),
            selected = paused,
            onClick = onPausedClick,
            icon = TaskState.STOPPED.icon()
        )
        ScanFilterChip(
            text = stringResource(Res.string.TaskStateChipFilter_Error),
            selected = error,
            onClick = onErrorClick,
            icon = TaskState.FAILED.icon()
        )
        ScanFilterChip(
            text = stringResource(Res.string.TaskStateChipFilter_Completed),
            selected = completed,
            onClick = onCompletedClick,
            icon = TaskState.COMPLETED.icon()
        )
    }
}
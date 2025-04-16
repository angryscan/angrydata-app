package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.*

@Composable
fun ScanStat(
    totalFiles: Long,
    selectedFiles: Long,
    foundFiles: Long,
    folderSize: String,
    scanTime: String,
    scoreSum: Long
) {
    // Total files count
    ScanStatItem(
        title = stringResource(Res.string.Task_TotalFiles),
        text = totalFiles.toString()
    )

    VerticalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )

    //Selected files count
    ScanStatItem(
        title = stringResource(Res.string.Task_SelectedFiles),
        text = selectedFiles.toString()
    )

    VerticalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )

    //Found files count
    ScanStatItem(
        title = stringResource(Res.string.Task_FoundFiles),
        text = foundFiles.toString()
    )

    VerticalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )

    //Folder size
    ScanStatItem(
        title = stringResource(Res.string.Task_FolderSize),
        text = folderSize
    )

    VerticalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )

    //Scan time
    ScanStatItem(
        title = stringResource(Res.string.Task_ScanTime),
        text = scanTime
    )

    VerticalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary
    )

    //Score sum of all found files
    ScanStatItem(
        title = stringResource(Res.string.Result_ColumnScore),
        text = scoreSum.toString()
    )
}
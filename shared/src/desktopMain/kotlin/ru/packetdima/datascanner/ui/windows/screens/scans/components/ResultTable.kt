package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.TaskEntityViewModel
import ru.packetdima.datascanner.scan.TaskFileResult
import ru.packetdima.datascanner.scan.TaskFilesViewModel
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultTable(taskFilesViewModel: TaskFilesViewModel, task: TaskEntityViewModel) {

    val coroutineScope = rememberCoroutineScope()

    val taskFiles by taskFilesViewModel.taskFiles.collectAsState()

    var sortColumn by remember { mutableStateOf(SortColumn.Score) }
    var sortDescending by remember { mutableStateOf(false) }

    val sortedFiles = taskFiles.sortedWith(
        if (sortDescending)
            sortColumn.comparator().reversed()
        else
            sortColumn.comparator()
    )


    val selectedFiles = remember { mutableStateListOf<Int>() }

    val filesExists = remember { mutableStateListOf<Int>() }

    val scrollState = rememberLazyListState()

    val scanned by task.scannedFiles.collectAsState()
    val skipped by task.skippedFiles.collectAsState()
    val selected by task.selectedFiles.collectAsState()

    val state by task.state.collectAsState()

    val progress = if (selected > 0) (scanned + skipped).toFloat() / selected else 0f

    var prevProgress by remember { mutableStateOf(progress) }

    LaunchedEffect(state) {
        filesExists.clear()
        filesExists.addAll(
            taskFiles
                .filter { File(it.path).exists() }
                .map { it.id }
        )
    }


    LaunchedEffect(progress) {
        if (progress > prevProgress + 0.1f) {
            taskFilesViewModel.update()
            prevProgress = progress
        }
    }

    LaunchedEffect(state) {
        taskFilesViewModel.update()
    }

    val snackbarHostState = remember { SnackbarHostState() }



    Scaffold(
        modifier = Modifier
            .clip(
                MaterialTheme.shapes.medium.copy(
                    bottomStart = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            )
            .background(MaterialTheme.colorScheme.surface),
        floatingActionButton = {
            if (selectedFiles.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val filesToDelete = selectedFiles.size
                            val filesDeleted = taskFiles.filter { it.id in selectedFiles }.map { file ->
                                File(file.path).delete()
                            }.count { it }

                            taskFiles.filter { it.id in selectedFiles }.forEach {
                                if (!File(it.path).exists()) {
                                    filesExists.remove(it.id)
                                }
                            }

                            coroutineScope.launch {
                                if (filesDeleted == filesToDelete)
                                    snackbarHostState.showSnackbar(
                                        getString(
                                            Res.string.Result_DeletedFiles,
                                            filesDeleted
                                        )
                                    )
                                else
                                    snackbarHostState.showSnackbar(
                                        getString(
                                            Res.string.Result_NotDeletedFiles,
                                            filesToDelete - filesDeleted
                                        )
                                    )
                            }

                            selectedFiles.clear()

                        }
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    ) {
                        Text(text = stringResource(Res.string.Result_DeleteFiles, selectedFiles.size))
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = if (scrollState.canScrollBackward || scrollState.canScrollForward) 30.dp else 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedFiles.isNotEmpty() && selectedFiles.containsAll(sortedFiles.map { it.id }),
                        onCheckedChange = { checkState ->
                            if (checkState) {
                                selectedFiles.addAll(
                                    sortedFiles.map { it.id }
                                        .filter { id -> !selectedFiles.contains(id) && id in filesExists }
                                )
                            } else {
                                selectedFiles.clear()
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        colors = CheckboxDefaults.colors().copy(
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                            uncheckedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .clip(shape = MaterialTheme.shapes.small)
                            .clickable {
                                if (sortColumn == SortColumn.Path) {
                                    sortDescending = !sortDescending
                                } else {
                                    sortColumn = SortColumn.Path
                                    sortDescending = false
                                }
                            }
                            .padding(2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.Result_ColumnFile),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (sortColumn == SortColumn.Path) {
                                Icon(
                                    imageVector = if (sortDescending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                                    contentDescription = "Sort",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .clip(shape = MaterialTheme.shapes.small)
                            .clickable {
                                if (sortColumn == SortColumn.Attribute) {
                                    sortDescending = !sortDescending
                                } else {
                                    sortColumn = SortColumn.Attribute
                                    sortDescending = false
                                }
                            }
                            .padding(2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.Result_ColumnAttributes),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (sortColumn == SortColumn.Attribute) {
                                Icon(
                                    imageVector = if (sortDescending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                                    contentDescription = "Sort",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.1f)
                            .clip(shape = MaterialTheme.shapes.small)
                            .clickable {
                                if (sortColumn == SortColumn.Score) {
                                    sortDescending = !sortDescending
                                } else {
                                    sortColumn = SortColumn.Score
                                    sortDescending = false
                                }
                            }
                            .padding(2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.Result_ColumnScore),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (sortColumn == SortColumn.Score) {
                                Icon(
                                    imageVector = if (sortDescending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                                    contentDescription = "Sort",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.1f)
                            .clip(shape = MaterialTheme.shapes.small)
                            .clickable {
                                if (sortColumn == SortColumn.Count) {
                                    sortDescending = !sortDescending
                                } else {
                                    sortColumn = SortColumn.Count
                                    sortDescending = false
                                }
                            }
                            .padding(2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.Result_ColumnCount),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (sortColumn == SortColumn.Count) {
                                Icon(
                                    imageVector = if (sortDescending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                                    contentDescription = "Sort",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.1f)
                            .clip(shape = MaterialTheme.shapes.small)
                            .clickable {
                                if (sortColumn == SortColumn.Size) {
                                    sortDescending = !sortDescending
                                } else {
                                    sortColumn = SortColumn.Size
                                    sortDescending = false
                                }
                            }
                            .padding(2.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.Result_ColumnSize),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (sortColumn == SortColumn.Size) {
                                Icon(
                                    imageVector = if (sortDescending) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                                    contentDescription = "Sort",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = scrollState
                ) {
                    items(sortedFiles) { file ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(4.dp)
                        ) {

                            Checkbox(
                                checked = selectedFiles.contains(file.id),
                                onCheckedChange = { checkState ->
                                    if (checkState) {
                                        selectedFiles.add(file.id)
                                    } else {
                                        selectedFiles.remove(file.id)
                                    }
                                },
                                modifier = Modifier.size(40.dp),
                                colors = CheckboxDefaults.colors().copy(
                                    checkedBorderColor = MaterialTheme.colorScheme.primary,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                enabled = filesExists.contains(file.id)
                            )
                            Text(
                                text = file.path
                                    .replace(task.path.value, "")
                                    .removePrefix("/")
                                    .removePrefix("\\")
                                    .let {
                                        if(it.isNotEmpty())
                                            it
                                        else file.path
                                            .substringAfterLast("/")
                                            .substringAfterLast("\\")
                                    },
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                                letterSpacing = 0.1.sp,
                                fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                modifier = Modifier.weight(0.5f),
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .weight(0.5f)
                            ) {
                                file.foundAttributes.forEach { attr ->
                                    AttributeCard(attr)
                                }
                            }
                            Text(
                                text = file.score.toString(),
                                modifier = Modifier.weight(0.1f)
                            )
                            Text(
                                text = file.count.toString(),
                                modifier = Modifier.weight(0.1f)
                            )
                            Text(
                                text = file.size.toString(),
                                modifier = Modifier.weight(0.1f)
                            )
                        }
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .width(10.dp)
                    .align(Alignment.CenterEnd),
                style = LocalScrollbarStyle.current.copy(
                    unhoverColor = MaterialTheme.colorScheme.secondary,
                    hoverColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

enum class SortColumn {
    Path,
    Attribute,
    Score,
    Count,
    Size
}

fun SortColumn.comparator(): Comparator<TaskFileResult> = when (this) {
    SortColumn.Path -> compareBy(TaskFileResult::path)
    SortColumn.Attribute -> compareBy<TaskFileResult> { it.foundAttributes.size }.reversed()
    SortColumn.Score -> compareByDescending(TaskFileResult::score)
    SortColumn.Count -> compareByDescending(TaskFileResult::count)
    SortColumn.Size -> compareByDescending(TaskFileResult::size)
}
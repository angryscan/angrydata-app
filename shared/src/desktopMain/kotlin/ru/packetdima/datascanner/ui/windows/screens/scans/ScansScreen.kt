package ru.packetdima.datascanner.ui.windows.screens.scans

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.ui.windows.screens.scans.components.ScanFilterChipBox
import ru.packetdima.datascanner.ui.windows.screens.scans.components.ScanTaskCard

@Composable
fun ScansScreen() {
    val scanService = koinInject<ScanService>()

    val filterTaskStates = remember { mutableListOf<TaskState>() }
    var active by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }

    val allTasks by scanService.tasks.tasks.collectAsState()

    val filteredTasks = allTasks.filter { task ->
        if (filterTaskStates.isEmpty())
            task.state.value != TaskState.LOADING
        else
            task.state.value in filterTaskStates
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .padding(bottom = 8.dp)
        ) {
            ScanFilterChipBox(
                active = active,
                paused = paused,
                error = error,
                completed = completed,
                onActiveClick = {
                    active = !active;
                    if (active) {
                        filterTaskStates.addAll(
                            listOf(
                                TaskState.SCANNING,
                                TaskState.SEARCHING,
                                TaskState.PENDING,
                            )
                        )
                    } else {
                        filterTaskStates.removeAll(
                            listOf(
                                TaskState.SCANNING,
                                TaskState.SEARCHING,
                                TaskState.PENDING,
                            )
                        )
                    }
                },
                onPausedClick = {
                    paused = !paused;
                    if (paused) {
                        filterTaskStates.add(TaskState.STOPPED)
                    } else {
                        filterTaskStates.remove(TaskState.STOPPED)
                    }
                },
                onErrorClick = {
                    error = !error;
                    if (error) {
                        filterTaskStates.add(TaskState.FAILED)
                    } else {
                        filterTaskStates.remove(TaskState.FAILED)
                    }
                },
                onCompletedClick = {
                    completed = !completed;
                    if (completed) {
                        filterTaskStates.add(TaskState.COMPLETED)
                    } else {
                        filterTaskStates.remove(TaskState.COMPLETED)
                    }
                }
            )
        }
        Box() {
            val state = rememberLazyListState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                state = state,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTasks) { task ->
                    ScanTaskCard(task)
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(state),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 10.dp)
                    .width(10.dp)
            )
        }

    }
}
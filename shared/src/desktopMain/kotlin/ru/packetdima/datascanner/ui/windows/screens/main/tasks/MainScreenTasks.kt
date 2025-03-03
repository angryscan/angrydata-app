package ru.packetdima.datascanner.ui.windows.screens.main.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.resources.MainScreen_ScanCurrentState
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.scan.ScanService

@Composable
fun MainScreenTasks(
    expanded: Boolean,
    onExpandedClick: () -> Unit
) {

    val scanService = koinInject<ScanService>()

    val allTasks by scanService.tasks.tasks.collectAsState()
    val activeTasks = allTasks.filter {
        it.state.value != TaskState.COMPLETED
    }

    val scanStateIconRotation = remember { Animatable(270f) }

    var currentTime by remember { mutableStateOf(Clock.System.now()) }

    LaunchedEffect(currentTime) {
        while (true) {
            currentTime = Clock.System.now()
            delay(1000)
        }
    }

    LaunchedEffect(expanded) {
        scanStateIconRotation.animateTo(if (expanded) 270f else 90f)
    }

    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(700.dp)
                .height(63.dp)
                .clip(
                    MaterialTheme.shapes.medium.copy(
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                )
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    onExpandedClick()
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
                        activeTasks.size
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
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
                    .padding(14.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                val height = activeTasks.size * 60 + activeTasks.size * 10 / 2 + (activeTasks.size % 2)
                LazyColumn(
                    modifier = Modifier
                        .height(height.dp)
                        .width(640.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeTasks) { task ->
                        MainScreenTaskCard(task, currentTime)
                    }
                }
            }
        }
    }
}
package ru.packetdima.datascanner.ui.windows.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.packetdima.datascanner.scan.common.ScanPathHelper

@Composable
fun MainScreen () {
    val helperPath = ScanPathHelper.path.collectAsState()

    var path by remember { mutableStateOf(helperPath.value) }

    LaunchedEffect(helperPath) {
        if(helperPath.value.isNotEmpty()) {
            path = helperPath.value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.7f))

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .height(80.dp)
                    .width(700.dp),
                value = path,
                onValueChange = {path = it},
                placeholder = { Text("Поиск") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colorScheme.onBackground)
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable{

                                },
                            contentAlignment = Alignment.Center
                        ){
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
            Row {
                Button(
                    onClick = {
                        /*TODO*/
                    },
                    modifier = Modifier
                        .width(268.dp)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
                ) {
                    Text(
                        text = "Сканировать",
                        fontSize = 24.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium.copy(topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)))
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable {
                            /*TODO*/
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberDialogState
import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.LocationWindow_Title
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.scan.common.files.Location
import ru.packetdima.datascanner.scan.common.files.LocationFinder
import ru.packetdima.datascanner.ui.strings.composableName
import ru.packetdima.datascanner.ui.windows.components.DesktopWindowShapes
import ru.packetdima.datascanner.ui.windows.components.TitleBar

@Composable
fun AttributeLocationWindow(
    filePath: String,
    attribute: IDetectFunction,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var locations by remember { mutableStateOf<List<Location>>(emptyList()) }

    var searching by remember { mutableStateOf(false) }

    coroutineScope.launch {
        searching = true
        locations = LocationFinder.findLocations(
            filePath,
            attribute
        )
        searching = false
    }


    val state = rememberDialogState(
        width = 800.dp,
        height = 400.dp
    )

    val scrollState = rememberLazyListState()

    DialogWindow(
        onCloseRequest = onClose,
        state = state,
        undecorated = true,
        resizable = false
    ) {
        Surface(
            shape = DesktopWindowShapes(),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleBar(
                    windowPlacement = WindowPlacement.Floating
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(Res.string.LocationWindow_Title, attribute.composableName()),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                            lineHeight = MaterialTheme.typography.titleMedium.lineHeight,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = onClose
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
                if(searching) {
                    CircularProgressIndicator()
                }
                else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .padding(start = 8.dp, end = if (scrollState.canScrollBackward || scrollState.canScrollForward) 20.dp else 8.dp),
                            state = scrollState
                        ) {
                            items(locations) { location ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(0.8f)
                                            .fillMaxWidth(),
                                        text = location.entry
                                    )
                                    Text(
                                        modifier = Modifier
                                            .weight(0.2f)
                                            .fillMaxWidth(),
                                        text = location.location
                                    )
                                }
                            }
                        }

                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 6.dp)
                                .width(10.dp),
                            style = LocalScrollbarStyle.current.copy(
                                hoverColor = MaterialTheme.colorScheme.primary,
                                unhoverColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }
    }
}
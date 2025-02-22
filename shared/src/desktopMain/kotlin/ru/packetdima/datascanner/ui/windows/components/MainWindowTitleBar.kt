package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope

@Composable
fun WindowScope.MainWindowTitleBar(
    windowPlacement: WindowPlacement,
    expanded: Boolean,
    onMinimizeClick: () -> Unit,
    onExpandClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    TitleBar(windowPlacement) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onMinimizeClick,
            ) {
                Icon(
                    Icons.Outlined.Minimize,
                    contentDescription = "Minimize",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = onExpandClick
            ) {
                Icon(
                    imageVector = if(expanded) Icons.Outlined.CloseFullscreen else Icons.Outlined.OpenInFull,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = onCloseClick
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
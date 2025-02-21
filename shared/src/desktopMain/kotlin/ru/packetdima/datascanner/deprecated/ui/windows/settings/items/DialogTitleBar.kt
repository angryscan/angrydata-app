package ru.packetdima.datascanner.deprecated.ui.windows.settings.items

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogWindowScope

@Composable
fun DialogWindowScope.DialogTitleBar(
    block: @Composable () -> Unit
) {
    WindowDraggableArea {
        block()
    }
}
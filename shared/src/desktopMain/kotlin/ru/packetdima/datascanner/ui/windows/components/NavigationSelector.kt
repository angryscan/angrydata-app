package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.ui.window.WindowPlacement

@Composable
fun NavigationSelector(
    navController: NavController,
    windowPlacement: WindowPlacement? = null,
    expanded: Boolean = false,
    onMinimizeClick: (() -> Unit)? = null,
    onExpandClick: (() -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null
) {
    TopNavigation(
        navController = navController,
        windowPlacement = windowPlacement,
        expanded = expanded,
        onMinimizeClick = onMinimizeClick,
        onExpandClick = onExpandClick,
        onCloseClick = onCloseClick
    )
}


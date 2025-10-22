package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.navigation.AppScreen
import ru.packetdima.datascanner.resources.*

@Composable
fun TopNavigation(
    navController: NavController,
    windowPlacement: androidx.compose.ui.window.WindowPlacement? = null,
    expanded: Boolean = false,
    onMinimizeClick: (() -> Unit)? = null,
    onExpandClick: (() -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppLogo()
                
                NavigationTabs(
                    navController = navController,
                    currentDestination = destination
                )
            }

            if (onMinimizeClick != null && onExpandClick != null && onCloseClick != null) {
                WindowControlButtons(
                    windowPlacement = windowPlacement,
                    expanded = expanded,
                    onMinimizeClick = onMinimizeClick,
                    onExpandClick = onExpandClick,
                    onCloseClick = onCloseClick
                )
            }
        }
    }
}

@Composable
private fun AppLogo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        ),
                        radius = 50f
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .border(
                    width = 1.5.dp,
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.icon),
                contentDescription = stringResource(Res.string.appName),
                modifier = Modifier
                    .size(28.dp)
                    .scale(1.15f)
            )
        }

        Text(
            text = stringResource(Res.string.appName),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NavigationTabs(
    navController: NavController,
    currentDestination: androidx.navigation.NavDestination?
) {
    val navigationItems = listOf(
        NavigationItem(
            route = AppScreen.Main,
            icon = painterResource(Res.drawable.SideMenu_IconMainPage),
            label = stringResource(Res.string.SideMenu_MainPage),
            isSelected = currentDestination?.hasRoute(AppScreen.Main::class) ?: false
        ),
        NavigationItem(
            route = AppScreen.Scans,
            icon = painterResource(Res.drawable.SideMenu_IconScans),
            label = stringResource(Res.string.SideMenu_ScanListPage),
            isSelected = currentDestination?.hasRoute(AppScreen.Scans::class) ?: false ||
                    currentDestination?.hasRoute(AppScreen.ScanResult::class) ?: false
        ),
        NavigationItem(
            route = AppScreen.Settings,
            icon = painterResource(Res.drawable.SideMenu_IconSettings),
            label = stringResource(Res.string.SideMenu_SettingsPage),
            isSelected = currentDestination?.hasRoute(AppScreen.Settings::class) ?: false
        )
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
    ) {
        navigationItems.forEach { item ->
            NavigationTab(
                item = item,
                onClick = { 
                    try {
                        println("Navigating to: ${item.route}")
                        if (!item.isSelected) {
                            navController.navigate(item.route)
                            println("Navigation successful")
                        } else {
                            println("Already on this screen")
                        }
                    } catch (e: Exception) {
                        println("Navigation error: ${e.message}")
                    }
                }
            )
        }
    }
}

@Composable
private fun NavigationTab(
    item: NavigationItem,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            item.isSelected -> 1.05f
            else -> 1f
        },
        animationSpec = tween(150),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (item.isSelected) 
                    MaterialTheme.colorScheme.primary
                else 
                    Color.Transparent
            )
            .clickable(
                enabled = true,
                onClick = {
                    isPressed = true
                    onClick()
                    coroutineScope.launch {
                        delay(100)
                        isPressed = false
                    }
                }
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(18.dp),
                tint = if (item.isSelected) 
                    MaterialTheme.colorScheme.onPrimary
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (item.isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (item.isSelected) 
                    MaterialTheme.colorScheme.onPrimary
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun WindowControlButtons(
    windowPlacement: androidx.compose.ui.window.WindowPlacement?,
    expanded: Boolean,
    onMinimizeClick: () -> Unit,
    onExpandClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp)
    ) {
        WindowControlButton(
            onClick = onMinimizeClick,
            icon = Icons.Outlined.Minimize,
            contentDescription = "Minimize",
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        )

        WindowControlButton(
            onClick = onExpandClick,
            icon = if (expanded) Icons.Outlined.CloseFullscreen else Icons.Outlined.OpenInFull,
            contentDescription = if (expanded) "Restore" else "Maximize",
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        )

        WindowControlButton(
            onClick = onCloseClick,
            icon = Icons.Outlined.Close,
            contentDescription = "Close",
            backgroundColor = MaterialTheme.colorScheme.errorContainer
        )
    }
}

@Composable
private fun WindowControlButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    backgroundColor: Color
) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .size(36.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                    coroutineScope.launch {
                        delay(100)
                        isPressed = false
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun NavigationActions() {
}

data class NavigationItem(
    val route: Any,
    val icon: Painter,
    val label: String,
    val isSelected: Boolean
)

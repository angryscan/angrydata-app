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
            .height(80.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppLogo()
            
            Spacer(modifier = Modifier.width(32.dp))

            NavigationTabs(
                navController = navController,
                currentDestination = destination
            )
            
            Spacer(modifier = Modifier.weight(1f))

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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.icon),
            contentDescription = stringResource(Res.string.appName),
            modifier = Modifier.size(40.dp)
        )
        
        Text(
            text = stringResource(Res.string.appName),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
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
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else 
                    Color.Transparent
            )
            .border(
                width = if (item.isSelected) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(20.dp),
                tint = if (item.isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (item.isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (item.isSelected) 
                    MaterialTheme.colorScheme.primary 
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMinimizeClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Outlined.Minimize,
                contentDescription = "Minimize",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        IconButton(
            onClick = onExpandClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Outlined.CloseFullscreen else Icons.Outlined.OpenInFull,
                contentDescription = if (expanded) "Restore" else "Maximize",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = "Close",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
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

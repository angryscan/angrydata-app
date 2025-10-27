package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloseFullscreen
import androidx.compose.material.icons.outlined.Minimize
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.navigation.AppScreen
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.ScanService

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
                AppLogo(navController = navController)
                
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
private fun AppLogo(
    navController: NavController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isMainScreen = currentDestination?.hasRoute(AppScreen.Main::class) ?: false
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isHovered by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.8f
            isHovered -> 0.95f
            else -> 1f
        },
        animationSpec = tween(150, easing = EaseInOutCubic),
        label = "alpha"
    )
    
    val elevation by animateFloatAsState(
        targetValue = when {
            isPressed -> 4f
            isHovered -> 12f
            else -> 8f
        },
        animationSpec = tween(200, easing = EaseInOutCubic),
        label = "elevation"
    )

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
                    elevation = elevation.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                .scale(scale)
                .alpha(alpha)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(
                        bounded = true,
                        radius = 200.dp
                    ),
                    onClick = {
                        try {
                            if (!isMainScreen) {
                                println("Logo clicked - navigating to Main")
                                navController.navigate(AppScreen.Main)
                                println("Navigation to Main successful")
                            } else {
                                println("Already on Main screen - no navigation needed")
                            }
                        } catch (e: Exception) {
                            println("Navigation error: ${e.message}")
                        }
                    }
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
    val scanService = koinInject<ScanService>()
    val allTasks by scanService.tasks.tasks.collectAsState()
    
    // Принудительно обновляем состояние каждую секунду для корректной работы индикатора
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Обновляем каждую секунду
            currentTime = System.currentTimeMillis()
        }
    }
    
    val hasActiveScans = remember(currentTime, allTasks) {
        allTasks.any { task ->
            val state = task.state.value
            state == TaskState.SCANNING || state == TaskState.SEARCHING
        }
    }
    
    val navigationItems = listOf(
        NavigationItem(
            route = AppScreen.Main,
            icon = painterResource(Res.drawable.SideMenu_IconMainPage),
            label = stringResource(Res.string.SideMenu_MainPage),
            isSelected = currentDestination?.hasRoute(AppScreen.Main::class) ?: false,
            hasActiveIndicator = false
        ),
        NavigationItem(
            route = AppScreen.Scans,
            icon = painterResource(Res.drawable.SideMenu_IconScans),
            label = stringResource(Res.string.SideMenu_ScanListPage),
            isSelected = currentDestination?.hasRoute(AppScreen.Scans::class) ?: false ||
                    currentDestination?.hasRoute(AppScreen.ScanResult::class) ?: false,
            hasActiveIndicator = hasActiveScans
        ),
        NavigationItem(
            route = AppScreen.Settings,
            icon = painterResource(Res.drawable.SideMenu_IconSettings),
            label = stringResource(Res.string.SideMenu_SettingsPage),
            isSelected = currentDestination?.hasRoute(AppScreen.Settings::class) ?: false,
            hasActiveIndicator = false
        )
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
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
                        if (item.route == AppScreen.Scans) {
                            val isOnScansScreen = currentDestination?.hasRoute(AppScreen.Scans::class) ?: false
                            if (!isOnScansScreen) {
                                navController.navigate(item.route)
                                println("Navigation to Scans successful")
                            } else {
                                println("Already on Scans screen - no navigation needed")
                            }
                        } else if (!item.isSelected) {
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isHovered by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            isHovered -> 1.05f
            item.isSelected -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.8f
            isHovered -> 0.98f
            else -> 1f
        },
        animationSpec = tween(150, easing = EaseInOutCubic),
        label = "alpha"
    )
    
    val elevation by animateFloatAsState(
        targetValue = when {
            isPressed -> 1f
            isHovered -> 6f
            item.isSelected -> 8f
            else -> 0f
        },
        animationSpec = tween(200, easing = EaseInOutCubic),
        label = "elevation"
    )
    
    val rotation by animateFloatAsState(
        targetValue = when {
            isPressed -> 1f
            isHovered -> -0.5f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .rotate(rotation)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (item.isSelected) 
                    MaterialTheme.colorScheme.primary
                else if (isHovered)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else 
                    Color.Transparent
            )
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .clickable(
                enabled = true,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = 200.dp
                ),
                onClick = {
                    onClick()
                }
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                val iconScale by animateFloatAsState(
                    targetValue = when {
                        isPressed -> 0.95f
                        isHovered -> 1.05f
                        item.isSelected -> 1.08f
                        else -> 1f
                    },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "iconScale"
                )
                
                val iconRotation by animateFloatAsState(
                    targetValue = when {
                        isPressed -> 2f
                        isHovered -> -1f
                        else -> 0f
                    },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "iconRotation"
                )
                
                Icon(
                    painter = item.icon,
                    contentDescription = item.label,
                    modifier = Modifier
                        .size(18.dp)
                        .scale(iconScale)
                        .rotate(iconRotation),
                    tint = if (item.isSelected) 
                        MaterialTheme.colorScheme.onPrimary
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                // Активный индикатор сканирования
                if (item.hasActiveIndicator) {
                    ActiveScanIndicator()
                }
            }
            
            val textScale by animateFloatAsState(
                targetValue = when {
                    isPressed -> 0.98f
                    isHovered -> 1.02f
                    item.isSelected -> 1.05f
                    else -> 1f
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "textScale"
            )
            
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (item.isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (item.isSelected) 
                    MaterialTheme.colorScheme.onPrimary
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                modifier = Modifier.scale(textScale)
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isHovered by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            isHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.8f
            isHovered -> 0.98f
            else -> 1f
        },
        animationSpec = tween(150, easing = EaseInOutCubic),
        label = "alpha"
    )
    
    val rotation by animateFloatAsState(
        targetValue = when {
            isPressed -> 2f
            isHovered -> -1f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )
    
    val elevation by animateFloatAsState(
        targetValue = when {
            isPressed -> 2f
            isHovered -> 8f
            else -> 0f
        },
        animationSpec = tween(200, easing = EaseInOutCubic),
        label = "elevation"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .rotate(rotation)
            .size(36.dp)
            .background(
                color = if (isHovered) 
                    backgroundColor.copy(alpha = 0.9f)
                else 
                    backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = backgroundColor.copy(alpha = 0.4f),
                spotColor = backgroundColor.copy(alpha = 0.5f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    radius = 50.dp
                ),
                onClick = {
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val iconScale by animateFloatAsState(
            targetValue = if (isPressed) 0.85f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            ),
            label = "iconScale"
        )
        
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(16.dp)
                .scale(iconScale),
            tint = if (isHovered)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.95f)
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun ActiveScanIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = Modifier
            .size(12.dp)
            .offset(x = 10.dp, y = (-10).dp)
    ) {
        // Внешний пульсирующий круг
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale)
                .alpha(pulseAlpha)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
        
        // Внутренний вращающийся индикатор
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        ),
                        radius = 6f
                    ),
                    shape = CircleShape
                )
        )
        
        // Центральная точка
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                )
                .align(Alignment.Center)
        )
    }
}

data class NavigationItem(
    val route: Any,
    val icon: Painter,
    val label: String,
    val isSelected: Boolean,
    val hasActiveIndicator: Boolean = false
)

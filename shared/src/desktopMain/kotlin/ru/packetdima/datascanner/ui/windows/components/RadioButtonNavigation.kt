package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Http
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.painterResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.aws_s3
import ru.packetdima.datascanner.ui.windows.screens.main.components.MainScreenConnector

@Composable
fun RadioButtonNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    
    val selectedRoute = when {
        destination?.hasRoute(MainScreenConnector.FileShare::class) == true -> MainScreenConnector.FileShare
        destination?.hasRoute(MainScreenConnector.S3::class) == true -> MainScreenConnector.S3
        destination?.hasRoute(MainScreenConnector.HTTP::class) == true -> MainScreenConnector.HTTP
        else -> MainScreenConnector.FileShare
    }
    
    val routes = listOf(
        MainScreenConnector.FileShare,
        MainScreenConnector.S3,
        MainScreenConnector.HTTP
    )
    
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        routes.forEach { route ->
            RadioButtonNavigationItem(
                isSelected = selectedRoute == route,
                icon = when (route) {
                    MainScreenConnector.FileShare -> rememberVectorPainter(Icons.Outlined.Folder)
                    MainScreenConnector.S3 -> painterResource(Res.drawable.aws_s3)
                    MainScreenConnector.HTTP -> rememberVectorPainter(Icons.Outlined.Http)
                    else -> rememberVectorPainter(Icons.Outlined.Folder)
                },
                text = when (route) {
                    MainScreenConnector.FileShare -> "File Share"
                    MainScreenConnector.S3 -> "AWS S3"
                    MainScreenConnector.HTTP -> "HTTP"
                    else -> "Unknown"
                },
                onClick = { 
                    if (selectedRoute != route) {
                        navController.navigate(route)
                    }
                }
            )
        }
    }
}

@Composable
private fun RadioButtonNavigationItem(
    isSelected: Boolean,
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isSelected -> 1.02f
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
            else -> 1f
        },
        animationSpec = tween(150),
        label = "alpha"
    )
    
    Row(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Radio button
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape
                        )
                )
            }
        }
        
        // Icon
        Icon(
            painter = icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp),
            tint = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        // Text
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

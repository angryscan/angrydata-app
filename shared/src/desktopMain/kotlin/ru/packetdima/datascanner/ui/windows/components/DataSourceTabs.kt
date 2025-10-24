package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.painterResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.aws_s3
import ru.packetdima.datascanner.ui.windows.screens.main.components.MainScreenConnector

@Composable
fun DataSourceTabs(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .width(IntrinsicSize.Min)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp)
    ) {
        // File Share tab
        DataSourceTab(
            isSelected = destination?.hasRoute(MainScreenConnector.FileShare::class) ?: false,
            icon = rememberVectorPainter(Icons.Outlined.Folder),
            text = "File share",
            onClick = { navController.navigate(MainScreenConnector.FileShare) }
        )
        
        // S3 tab
        DataSourceTab(
            isSelected = destination?.hasRoute(MainScreenConnector.S3::class) ?: false,
            icon = painterResource(Res.drawable.aws_s3),
            text = "AWS S3",
            onClick = { navController.navigate(MainScreenConnector.S3) }
        )
        
        // HTTP tab
        DataSourceTab(
            isSelected = destination?.hasRoute(MainScreenConnector.HTTP::class) ?: false,
            icon = rememberVectorPainter(Icons.Outlined.Http),
            text = "HTTP",
            onClick = { navController.navigate(MainScreenConnector.HTTP) }
        )
    }
}

@Composable
private fun DataSourceTab(
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
            isSelected -> 1.05f
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
    
    Box(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
            .background(
                color = if (isSelected) 
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else 
                    Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .width(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(18.dp),
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

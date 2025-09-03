package ru.packetdima.datascanner.ui.windows.screens.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Http
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.painterResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.aws_s3

@Composable
fun UpperMenu(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MainScreens.valueOf(
        backStackEntry?.destination?.route?.substringBefore("/") ?: MainScreens.FileShare.name
    )

    Surface(
        shape = MaterialTheme.shapes.medium
            .copy(topEnd = CornerSize(0.dp), topStart = CornerSize(0.dp)),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .animateContentSize()
                .width(IntrinsicSize.Min)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            UpperMenuItem(
                isSelected = currentScreen == MainScreens.FileShare,
                expanded = false,
                icon = rememberVectorPainter(Icons.Outlined.Folder),
                text = "File share",
                onClick = { navController.navigate(MainScreens.FileShare.name) }
            )
            UpperMenuItem(
                isSelected = currentScreen == MainScreens.S3,
                expanded = false,
                icon = painterResource(Res.drawable.aws_s3),
                text = "AWS S3",
                onClick = { navController.navigate(MainScreens.S3.name) }
            )
            UpperMenuItem(
                isSelected = currentScreen == MainScreens.HTTP,
                expanded = false,
                icon = rememberVectorPainter(Icons.Outlined.Http),
                text = "HTTP",
                onClick = { navController.navigate(MainScreens.HTTP.name) }
            )
        }
    }
}

@Composable
fun UpperMenuItem(
    isSelected: Boolean,
    expanded: Boolean,
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    iconSize: Dp = 48.dp,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable(
                enabled = enabled && !isSelected,
                onClick = onClick
            )
            .padding(14.dp, 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(iconSize),
            )
            AnimatedVisibility(
                expanded
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp
                )
            }
        }
    }
}
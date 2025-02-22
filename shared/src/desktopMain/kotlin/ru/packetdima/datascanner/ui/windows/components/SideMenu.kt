package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.*

@Composable
fun SideMenu() {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring()
            )
            .width(
                if (expanded) 336.dp else 88.dp
            )
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                expanded = !expanded
                            },
                    )
                    if (expanded) {
                        Text(
                            text = stringResource(Res.string.appName),
                            fontSize = 20.sp
                        )
                    }
                }
                if (expanded) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable {
                                expanded = !expanded
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = null,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            SideMenuItem(
                isSelected = true,
                expanded = expanded,
                icon = painterResource(Res.drawable.SideMenuIconMain),
                text = stringResource(Res.string.appName),
                onClick = { /*TODO*/ },
            )
        }
    }
}

@Composable
fun SideMenuItem(
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
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(14.dp, 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = text,
                modifier = Modifier.size(iconSize),
            )
            if (expanded) {
                Text(
                    text = text,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
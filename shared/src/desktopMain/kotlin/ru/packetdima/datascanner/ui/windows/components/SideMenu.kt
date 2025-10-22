package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.navigation.AppScreen
import ru.packetdima.datascanner.resources.*

@Composable
fun SideMenu(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .animateContentSize()
            .width(if (expanded) 336.dp else 88.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxHeight()
                .width(IntrinsicSize.Min)
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            if (!(destination?.hasRoute(AppScreen.Main::class) ?: false)) {
                                navController.navigate(AppScreen.Main)
                            }
                        },
                )
            }
            
            AnimatedVisibility(expanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.appName),
                        fontSize = 20.sp,
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .clickable {
                                if (!(destination?.hasRoute(AppScreen.Main::class) ?: false)) {
                                    navController.navigate(AppScreen.Main)
                                }
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
            Spacer(modifier = Modifier.height(18.dp))


            DescriptionTooltip(
                description = stringResource(Res.string.SideMenu_ScanListPage),
                delay = 1000
            ) {
                SideMenuItem(
                    isSelected = destination?.hasRoute(AppScreen.Scans::class) ?: false ||
                            destination?.hasRoute(AppScreen.ScanResult::class) ?: false,
                    expanded = expanded,
                    icon = painterResource(Res.drawable.SideMenu_IconScans),
                    text = stringResource(Res.string.SideMenu_ScanListPage),
                    onClick = { navController.navigate(AppScreen.Scans) },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            DescriptionTooltip(
                description = stringResource(Res.string.SideMenu_SettingsPage),
                delay = 1000
            ) {
                SideMenuItem(
                    isSelected = destination?.hasRoute(AppScreen.Settings::class) ?: false,
                    expanded = expanded,
                    icon = painterResource(Res.drawable.SideMenu_IconSettings),
                    text = stringResource(Res.string.SideMenu_SettingsPage),
                    onClick = { navController.navigate(AppScreen.Settings) },
                )
            }

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
            .height(60.dp)
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
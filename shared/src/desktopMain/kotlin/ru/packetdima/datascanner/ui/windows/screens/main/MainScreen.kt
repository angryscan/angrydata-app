package ru.packetdima.datascanner.ui.windows.screens.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.packetdima.datascanner.ui.windows.screens.main.components.MainScreens
import ru.packetdima.datascanner.ui.windows.screens.main.components.UpperMenu
import ru.packetdima.datascanner.ui.windows.screens.main.subscreens.FileShareScreen
import ru.packetdima.datascanner.ui.windows.screens.main.subscreens.HTTPScreen
import ru.packetdima.datascanner.ui.windows.screens.main.subscreens.S3Screen


@Composable
fun MainScreen(
    showScan:() -> Unit
) {
    var settingsExpanded by remember { mutableStateOf(false) }

    var scanStateExpanded by remember { mutableStateOf(false) }

    val navController = rememberNavController()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        UpperMenu(
            navController,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(IntrinsicSize.Min)
                .padding(horizontal = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(
                modifier = Modifier.height(
                    if(settingsExpanded ) 78.dp else 12.dp
                )
                    .animateContentSize()
            )

            NavHost(
                navController = navController,
                startDestination = MainScreens.FileShare.name,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                    ) + fadeIn(tween(700))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                    ) + fadeOut(tween(700))
                }
            ) {
                composable(route = MainScreens.FileShare.name) {
                    FileShareScreen(
                        settingsExpanded = settingsExpanded,
                        expandSettings = {
                            if (scanStateExpanded)
                                scanStateExpanded = false
                            settingsExpanded = true
                        },
                        hideSettings = {
                            settingsExpanded = false
                        },
                        expandScanState = showScan
                    )
                }
                composable(route = MainScreens.S3.name) {
                    S3Screen(
                        settingsExpanded = settingsExpanded,
                        expandSettings = {
                            if (scanStateExpanded)
                                scanStateExpanded = false
                            settingsExpanded = true
                        },
                        hideSettings = {
                            settingsExpanded = false
                        },
                        expandScanState = showScan
                    )
                }
                composable(route = MainScreens.HTTP.name) {
                    HTTPScreen(
                        settingsExpanded = settingsExpanded,
                        expandSettings = {
                            if (scanStateExpanded)
                                scanStateExpanded = false
                            settingsExpanded = true
                        },
                        hideSettings = {
                            settingsExpanded = false
                        },
                        expandScanState = showScan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }


    }
}


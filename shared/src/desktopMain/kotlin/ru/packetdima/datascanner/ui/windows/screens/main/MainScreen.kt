package ru.packetdima.datascanner.ui.windows.screens.main

import androidx.compose.animation.AnimatedContentTransitionScope
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
import ru.packetdima.datascanner.ui.windows.components.DataSourceTabs
import ru.packetdima.datascanner.ui.windows.screens.main.components.MainScreenConnector
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
        modifier = Modifier.fillMaxSize()
    ) {
        // Основной контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            NavHost(
                navController = navController,
                startDestination = MainScreenConnector.FileShare,
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
                composable<MainScreenConnector.FileShare> {
                    FileShareScreen(
                        navController = navController,
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
                composable<MainScreenConnector.S3> {
                    S3Screen(
                        navController = navController,
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
                composable<MainScreenConnector.HTTP> {
                    HTTPScreen(
                        navController = navController,
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

            Spacer(modifier = Modifier.height(76.dp))
        }
        
        // Вкладки источников данных в правом нижнем углу
        DataSourceTabs(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(IntrinsicSize.Min)
        )
    }
}


package ru.packetdima.datascanner.ui.windows.screens.main.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Transition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.ScanSettings_FastScan
import ru.packetdima.datascanner.resources.ScanSettings_Tooltip_FastScan
import ru.packetdima.datascanner.ui.windows.components.DescriptionTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBox(
    transition: Transition<Boolean>,
    height: Dp
) {
    val scanSettings = koinInject<ScanSettings>()

    var fastScan by remember { scanSettings.fastScan }

    LaunchedEffect(fastScan) {
        scanSettings.save()
    }

    val scrollState = rememberScrollState()

    AnimatedVisibility(
        transition.currentState,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .height(height)
                .padding(6.dp)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(
                        end = if (scrollState.canScrollBackward || scrollState.canScrollForward)
                            30.dp
                        else
                            0.dp
                    )
                    .verticalScroll(scrollState)
            ) {
                // Fast scan checkbox
                DescriptionTooltip(
                    description = stringResource(Res.string.ScanSettings_Tooltip_FastScan),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = fastScan,
                            onCheckedChange = { fastScan = it }
                        )
                        CompositionLocalProvider(LocalRippleConfiguration provides null) {
                            Text(
                                text = stringResource(Res.string.ScanSettings_FastScan),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    fastScan = !fastScan
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ScanSettings_FastScan),
                                contentDescription = null
                            )
                        }
                    }
                }

                // File extensions selection
                SettingsBoxExtensionsSelection(scanSettings)

                // Detect functions selection
                SettingsBoxDetectFunctions(scanSettings)

                // User signatures selection
                SettingsBoxUserSignature(scanSettings)
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(end = 10.dp)
                    .width(10.dp),
                style = LocalScrollbarStyle.current.copy(
                    hoverColor = MaterialTheme.colorScheme.primary,
                    unhoverColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}
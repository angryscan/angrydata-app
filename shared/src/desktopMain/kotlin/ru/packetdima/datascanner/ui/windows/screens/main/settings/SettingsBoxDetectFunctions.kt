package ru.packetdima.datascanner.ui.windows.screens.main.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.downdetector.bigdatascanner.common.DetectFunction
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.ScanSettings_DetectFunctions
import ru.packetdima.datascanner.resources.ScanSettings_SelectAll
import ru.packetdima.datascanner.scan.common.FileType
import ru.packetdima.datascanner.ui.strings.composableName
import ru.packetdima.datascanner.ui.windows.components.DetectFunctionTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBoxDetectFunctions(scanSettings: ScanSettings) {
    val detectFunctions = remember { scanSettings.detectFunctions }
    var expanded by remember { scanSettings.detectFunctionsExpanded }

    LaunchedEffect(detectFunctions, expanded) {
        scanSettings.save()
    }

    SettingsBoxSpan(
        text = stringResource(Res.string.ScanSettings_DetectFunctions),
        expanded = expanded,
        onExpandClick = {
            expanded = !expanded
        }
    ) {
        val rows = FileType.entries.size / 3 + if (FileType.entries.size % 3 > 0) 1 else 0

        val height = (24 * rows + (6 * (rows - 1))).dp + 52.dp + 24.dp

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(42.dp)
                ) {
                    Checkbox(
                        checked = scanSettings.detectFunctions.containsAll(DetectFunction.entries),
                        onCheckedChange = { checked ->
                            if (checked) {
                                scanSettings.detectFunctions.addAll(DetectFunction.entries.filter {
                                    !scanSettings.detectFunctions.contains(
                                        it
                                    )
                                })
                            } else {
                                scanSettings.detectFunctions.clear()
                            }
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = stringResource(Res.string.ScanSettings_SelectAll),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if (!scanSettings.detectFunctions.containsAll(DetectFunction.entries))
                                    scanSettings.detectFunctions.addAll(DetectFunction.entries.filter {
                                        !scanSettings.detectFunctions.contains(
                                            it
                                        )
                                    })
                                else
                                    scanSettings.detectFunctions.clear()
                                scanSettings.save()
                            }
                        )
                    }
                }
            }
            items(DetectFunction.entries) { detectFunction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(24.dp)
                ) {
                    Checkbox(
                        checked = scanSettings.detectFunctions.contains(detectFunction),
                        onCheckedChange = { checked ->
                            if (checked && !scanSettings.detectFunctions.contains(detectFunction))
                                scanSettings.detectFunctions.add(detectFunction)
                            else if (!checked)
                                scanSettings.detectFunctions.remove(detectFunction)
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        DetectFunctionTooltip(
                            detectFunction = detectFunction
                        ) {
                            Text(
                                text = detectFunction.composableName(),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    if (scanSettings.detectFunctions.contains(detectFunction))
                                        scanSettings.detectFunctions.remove(detectFunction)
                                    else
                                        scanSettings.detectFunctions.add(detectFunction)
                                    scanSettings.save()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
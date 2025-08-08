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
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.ui.strings.composableName
import ru.packetdima.datascanner.ui.windows.components.DetectFunctionTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBoxDetectFunctions(
    scanSettings: ScanSettings
) {
    val detectFunctions = remember { scanSettings.detectFunctions }
    var expanded by remember { scanSettings.detectFunctionsExpanded }
    var detectCode by remember { scanSettings.detectCode }
    var detectCert by remember { scanSettings.detectCert }
    var detectDomains by remember { scanSettings.detectBlockedDomains }

    LaunchedEffect(detectFunctions, expanded, detectCert, detectCode, detectDomains) {
        scanSettings.save()
    }

    SettingsBoxSpan(
        text = stringResource(Res.string.ScanSettings_DetectFunctions),
        expanded = expanded,
        onExpandClick = {
            expanded = !expanded
        }
    ) {
        val size = DetectFunction.entries.size + 2
        val rows = size / 3 + if (size % 3 > 0) 1 else 0

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
                        checked = scanSettings.detectFunctions.containsAll(DetectFunction.entries)
                                && scanSettings.detectCert.value
                                && scanSettings.detectCode.value,
                        onCheckedChange = { checked ->
                            if (checked) {
                                scanSettings.detectFunctions.addAll(DetectFunction.entries.filter {
                                    !scanSettings.detectFunctions.contains(
                                        it
                                    )
                                })
                                scanSettings.detectCert.value = true
                                scanSettings.detectCode.value = true
                            } else {
                                scanSettings.detectFunctions.clear()
                                scanSettings.detectCert.value = false
                                scanSettings.detectCode.value = false
                            }
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = stringResource(Res.string.ScanSettings_SelectAll),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if (!scanSettings.detectFunctions.containsAll(DetectFunction.entries)) {
                                    scanSettings.detectFunctions.addAll(DetectFunction.entries.filter {
                                        !scanSettings.detectFunctions.contains(
                                            it
                                        )
                                    })
                                    scanSettings.detectCert.value = true
                                    scanSettings.detectCode.value = true
                                } else {
                                    scanSettings.detectFunctions.clear()
                                    scanSettings.detectCert.value = false
                                    scanSettings.detectCode.value = false
                                }
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
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(24.dp)
                ) {
                    Checkbox(
                        checked = scanSettings.detectCode.value,
                        onCheckedChange = { checked ->
                            scanSettings.detectCode.value = checked
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        DetectFunctionTooltip(
                            description = stringResource(Res.string.DetectFunction_Description_Code)
                        ) {
                            Text(
                                text = stringResource(Res.string.DetectFunction_Code),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    scanSettings.detectCode.value = !scanSettings.detectCode.value
                                    scanSettings.save()
                                }
                            )
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(24.dp)
                ) {
                    Checkbox(
                        checked = scanSettings.detectCert.value,
                        onCheckedChange = { checked ->
                            scanSettings.detectCert.value = checked
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        DetectFunctionTooltip(
                            description = stringResource(Res.string.DetectFunction_Description_Cert)
                        ) {
                            Text(
                                text = stringResource(Res.string.DetectFunction_Cert),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    scanSettings.detectCert.value = !scanSettings.detectCert.value
                                    scanSettings.save()
                                }
                            )
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(24.dp)
                ) {
                    Checkbox(
                        checked = scanSettings.detectBlockedDomains.value,
                        onCheckedChange = { checked ->
                            scanSettings.detectBlockedDomains.value = checked
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        DetectFunctionTooltip(
                            description = stringResource(Res.string.DetectFunction_Description_DetectBlockedDomains)
                        ) {
                            Text(
                                text = stringResource(Res.string.DetectFunction_DetectBlockedDomains),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    scanSettings.detectBlockedDomains.value = !scanSettings.detectBlockedDomains.value
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
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
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun
import ru.packetdima.datascanner.scan.functions.MatchersRegister
import ru.packetdima.datascanner.scan.functions.RKNDomainDetectFun
import ru.packetdima.datascanner.ui.strings.composableName
import ru.packetdima.datascanner.ui.windows.components.MatcherTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBoxDetectFunctions(
    scanSettings: ScanSettings
) {
    val matchers = remember { scanSettings.matchers }
    var expanded by remember { scanSettings.matchersSettingsExpanded }
    var detectCode by remember { scanSettings.detectCode }
    var detectCert by remember { scanSettings.detectCert }
    var detectBlockedDomains by remember { scanSettings.detectBlockedDomains }

    SettingsBoxSpan(
        text = stringResource(Res.string.ScanSettings_DetectFunctions),
        expanded = expanded,
        onExpandClick = {
            expanded = !expanded
            scanSettings.save()
        }
    ) {
        val size = MatchersRegister.matchers.size + 2
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
                        checked = matchers.containsAll(MatchersRegister.matchers)
                                && detectCert
                                && detectCode
                                && detectBlockedDomains,
                        onCheckedChange = { checked ->
                            if (checked) { // Select all detect functions
                                matchers.addAll(MatchersRegister.matchers.filter {
                                    !matchers.contains(
                                        it
                                    )
                                })
                                detectCert = true
                                detectCode = true
                                detectBlockedDomains = true
                            } else { // Deselect all detect functions
                                matchers.clear()
                                detectCert = false
                                detectCode = false
                                detectBlockedDomains = false
                            }
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = stringResource(Res.string.ScanSettings_SelectAll),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if (!matchers.containsAll(MatchersRegister.matchers)) {
                                    matchers.addAll(MatchersRegister.matchers.filter {
                                        !matchers.contains(
                                            it
                                        )
                                    })
                                    detectCert = true
                                    detectCode = true
                                    detectBlockedDomains = true
                                } else {
                                    matchers.clear()
                                    detectCert = false
                                    detectCode = false
                                    detectBlockedDomains = false
                                }
                                scanSettings.save()
                            }
                        )
                    }
                }
            }
            items(MatchersRegister.matchers) { matcher ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(24.dp)
                ) {
                    Checkbox(
                        checked = matchers.contains(matcher),
                        onCheckedChange = { checked ->
                            if (checked && !matchers.contains(matcher))
                                matchers.add(matcher)
                            else if (!checked)
                                matchers.remove(matcher)
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        MatcherTooltip(
                            matcher = matcher
                        ) {
                            Text(
                                text = matcher.composableName(),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    if (matchers.contains(matcher))
                                        matchers.remove(matcher)
                                    else
                                        matchers.add(matcher)
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
                            detectCode = checked
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        MatcherTooltip(
                            matcher = CodeDetectFun
                        ) {
                            Text(
                                text = stringResource(Res.string.Matcher_Code),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    detectCode = !scanSettings.detectCode.value
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
                        checked = detectCert,
                        onCheckedChange = { checked ->
                            detectCert = checked
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        MatcherTooltip(
                            matcher = CertDetectFun
                        ) {
                            Text(
                                text = stringResource(Res.string.Matcher_Cert),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    detectCert = !detectCert
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
                        checked = detectBlockedDomains,
                        onCheckedChange = { checked ->
                            detectBlockedDomains = checked
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        MatcherTooltip(
                            matcher = RKNDomainDetectFun
                        ) {
                            Text(
                                text = stringResource(Res.string.Matcher_DetectBlockedDomains),
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    detectBlockedDomains = !detectBlockedDomains
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
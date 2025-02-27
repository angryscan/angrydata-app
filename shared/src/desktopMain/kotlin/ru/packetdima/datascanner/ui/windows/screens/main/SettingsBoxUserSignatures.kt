package ru.packetdima.datascanner.ui.windows.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.common.UserSignatureSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.ScanSettings_Add
import ru.packetdima.datascanner.resources.ScanSettings_SelectAll
import ru.packetdima.datascanner.resources.ScanSettings_UserSignatures
import ru.packetdima.datascanner.scan.common.FileType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBoxUserSignature(scanSettings: ScanSettings) {
    val userSignatures = remember { scanSettings.userSignatures }
    var expanded by remember { scanSettings.userSignatureExpanded }
    
    val userSignatureSettings = koinInject<UserSignatureSettings>()
    
    LaunchedEffect(userSignatures, expanded) {
        scanSettings.save()
    }

    SettingsBoxSpan(
        text = stringResource(Res.string.ScanSettings_UserSignatures),
        expanded = expanded,
        onExpandClick = {
            expanded = !expanded
        },
        textTail = {
            IconButton(
                onClick = {
                    //TODO
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ScanSettings_Add),
                    contentDescription = null
                )
            }
        }
    ) {
        val rows = FileType.entries.size / 3 + if (FileType.entries.size % 3 > 0) 1 else 0

        val height = (24 * rows + (6 * (rows - 1))).dp + 52.dp

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
                        checked = userSignatures.containsAll(userSignatureSettings.userSignatures),
                        onCheckedChange = { checked ->
                            if (checked) {
                                userSignatures.addAll(userSignatureSettings.userSignatures.filter { !userSignatures.contains(it) })
                            } else {
                                userSignatures.clear()
                            }
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = stringResource(Res.string.ScanSettings_SelectAll),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if(!userSignatures.containsAll(userSignatureSettings.userSignatures))
                                    userSignatures.addAll(userSignatureSettings.userSignatures.filter { !userSignatures.contains(it) })
                                else
                                    userSignatures.clear()
                            }
                        )
                    }
                }
            }
            items(userSignatureSettings.userSignatures) { userSignature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(24.dp)
                ) {
                    Checkbox(
                        checked = userSignatures.contains(userSignature),
                        onCheckedChange = { checked ->
                            if (checked && !userSignatures.contains(userSignature))
                                userSignatures.add(userSignature)
                            else if (!checked)
                                userSignatures.remove(userSignature)
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = userSignature.name,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if(userSignatures.contains(userSignature))
                                    userSignatures.remove(userSignature)
                                else
                                    userSignatures.add(userSignature)
                            }
                        )
                    }
                }
            }
        }
    }
}
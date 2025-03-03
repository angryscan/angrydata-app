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
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.ScanSettings_FileExtensions
import ru.packetdima.datascanner.resources.ScanSettings_SelectAll
import ru.packetdima.datascanner.scan.common.FileType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBoxExtensionsSelection(scanSettings: ScanSettings) {
    var expanded by remember { scanSettings.extensionsExpanded }


    SettingsBoxSpan(
        text = stringResource(Res.string.ScanSettings_FileExtensions),
        expanded = expanded,
        onExpandClick = {
            expanded = !expanded
        }
    ) {
        val rows = FileType.entries.size / 5 + if (FileType.entries.size % 5 > 0) 1 else 0

        val height = (24 * rows + (6 * (rows - 1))).dp + 52.dp

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
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
                        checked = scanSettings.extensions.containsAll(FileType.entries),
                        onCheckedChange = { checked ->
                            if (checked) {
                                scanSettings.extensions.addAll(FileType.entries.filter { !scanSettings.extensions.contains(it) })
                            } else {
                                scanSettings.extensions.clear()
                            }
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = stringResource(Res.string.ScanSettings_SelectAll),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if(!scanSettings.extensions.containsAll(FileType.entries))
                                    scanSettings.extensions.addAll(FileType.entries.filter { !scanSettings.extensions.contains(it) })
                                else
                                    scanSettings.extensions.clear()
                                scanSettings.save()
                            }
                        )
                    }
                }
            }
            items(FileType.entries) { extension ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(24.dp)
                ) {
                    Checkbox(
                        checked = scanSettings.extensions.contains(extension),
                        onCheckedChange = { checked ->
                            if (checked && !scanSettings.extensions.contains(extension))
                                scanSettings.extensions.add(extension)
                            else if (!checked)
                                scanSettings.extensions.remove(extension)
                            scanSettings.save()
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = extension.name,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if(scanSettings.extensions.contains(extension))
                                    scanSettings.extensions.remove(extension)
                                else
                                    scanSettings.extensions.add(extension)
                                scanSettings.save()
                            }
                        )
                    }
                }
            }
        }
    }
}
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
    val extensions = remember { scanSettings.extensions }
    var expanded by remember { scanSettings.extensionsExpanded }

    LaunchedEffect(extensions, expanded) {
        scanSettings.save()
    }

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
                        checked = extensions.containsAll(FileType.entries),
                        onCheckedChange = { checked ->
                            if (checked) {
                                extensions.addAll(FileType.entries.filter { !extensions.contains(it) })
                            } else {
                                extensions.clear()
                            }
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = stringResource(Res.string.ScanSettings_SelectAll),
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if(!extensions.containsAll(FileType.entries))
                                    extensions.addAll(FileType.entries.filter { !extensions.contains(it) })
                                else
                                    extensions.clear()
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
                        checked = extensions.contains(extension),
                        onCheckedChange = { checked ->
                            if (checked && !extensions.contains(extension))
                                extensions.add(extension)
                            else if (!checked)
                                extensions.remove(extension)
                        }
                    )
                    CompositionLocalProvider(LocalRippleConfiguration provides null) {
                        Text(
                            text = extension.name,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                if(extensions.contains(extension))
                                    extensions.remove(extension)
                                else
                                    extensions.add(extension)
                            }
                        )
                    }
                }
            }
        }
    }
}
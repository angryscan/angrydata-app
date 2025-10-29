package ru.packetdima.datascanner.ui.windows.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.ScanSettings_DebugMode
import ru.packetdima.datascanner.resources.SettingsScreen_Logging
import ru.packetdima.datascanner.resources.SettingsScreen_OpenFolder
import java.awt.Desktop

@Composable
fun LoggingSettings() {
    val appSettings = koinInject<AppSettings>()
    var debugModeEnabled by remember { appSettings.debugMode }

    SettingsRow(
        title = stringResource(Res.string.SettingsScreen_Logging)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(Res.string.ScanSettings_DebugMode))

                Switch(
                    checked = debugModeEnabled,
                    onCheckedChange = {
                        debugModeEnabled = it
                        appSettings.save()
                    }
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .size(width = 150.dp, height = 34.dp),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                onClick = {
                    Desktop.getDesktop().open(AppFiles.LoggingDir.toFile())
                },
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(Res.string.SettingsScreen_OpenFolder),
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


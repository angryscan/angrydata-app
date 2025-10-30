package ru.packetdima.datascanner.ui.windows.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.rememberDialogState
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.About_Description
import ru.packetdima.datascanner.resources.About_License
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.SideMenu_AboutPage
import ru.packetdima.datascanner.ui.dialogs.DescriptionDialog
import ru.packetdima.datascanner.ui.dialogs.LicenseDialog

@Composable
fun AboutSettings() {
    var showDescriptionDialog by remember { mutableStateOf(false) }
    val descriptionDialogState = rememberDialogState(width = 600.dp, height = 450.dp)

    var showLicenseDialog by remember { mutableStateOf(false) }
    val licenseDialogState = rememberDialogState(width = 600.dp, height = 580.dp)

    SettingsRow(title = stringResource(Res.string.SideMenu_AboutPage)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .size(width = 140.dp, height = 40.dp),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                onClick = { showDescriptionDialog = true },
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(Res.string.About_Description),
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .size(width = 120.dp, height = 40.dp),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                onClick = { showLicenseDialog = true },
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(Res.string.About_License),
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                )
            }
        }
    }

    if (showDescriptionDialog) {
        DescriptionDialog(
            onCloseRequest = { showDescriptionDialog = false },
            dialogState = descriptionDialogState
        )
    }

    if (showLicenseDialog) {
        LicenseDialog(
            onCloseRequest = { showLicenseDialog = false },
            dialogState = licenseDialogState
        )
    }
}


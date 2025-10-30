package ru.packetdima.datascanner.ui.windows.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.angryscan.common.engine.hyperscan.HyperScanEngine
import org.angryscan.common.engine.kotlin.KotlinEngine
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.store.ContextMenu
import ru.packetdima.datascanner.ui.icons.icon
import ru.packetdima.datascanner.ui.strings.composableName
import java.awt.Desktop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val appSettings = koinInject<AppSettings>()
    val language by remember { appSettings.language }

    key(language) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .width(760.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(6.dp)
            ) {
                ThreadCountSettings()
                ContextMenuSettings()
                LanguageSettings()
                ThemeSettings()
                LoggingSettings()
                AboutSettings()
            }
        }
    }
    }
}
package ru.packetdima.datascanner.ui.windows.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.SettingsScreen_Language

@Composable
fun LanguageSettings() {
    val appSettings = koinInject<AppSettings>()
    var language by remember { appSettings.language }

    SettingsRow(title = stringResource(Res.string.SettingsScreen_Language)) {
        val rows =
            AppSettings.LanguageType.entries.size / 3 + if (AppSettings.LanguageType.entries.size % 3 > 0) 1 else 0

        val height = (34 * rows + (6 * (rows - 1))).dp

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
        ) {
            items(AppSettings.LanguageType.entries) { lang ->
                Box(
                    modifier = Modifier
                        .size(width = 150.dp, height = 34.dp)
                        .clip(
                            MaterialTheme.shapes.large
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.large
                        )
                        .background(
                            color = if (lang == language) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        )
                        .clickable(
                            enabled = lang != language,
                            onClick = {
                                language = lang
                                appSettings.save()
                            }
                        )
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lang.text,
                        fontSize = 14.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}


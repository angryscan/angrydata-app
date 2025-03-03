package ru.packetdima.datascanner.ui.windows.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.AppVersion
import ru.packetdima.datascanner.resources.*

@Composable
fun AboutScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp)
                    )
                    Text(text = stringResource(Res.string.appName), style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = stringResource(Res.string.AboutScreen_Description),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(Res.string.AboutScreen_Version, AppVersion),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(text = stringResource(Res.string.AboutScren_Copyright), style = MaterialTheme.typography.bodyLarge)
            }
        }

    }
}
package ru.packetdima.datascanner.deprecated.ui.windows.items

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloseFullscreen
import androidx.compose.material.icons.outlined.Minimize
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPlacement
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.plannerWindowTitle

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PlannerWindowTitleBarView(
    windowPlacement: WindowPlacement,
    onCloseApp: () -> Unit,
    onMinimize: () -> Unit,
    onMaximize: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                stringResource(Res.string.plannerWindowTitle),
                fontSize = 17.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colors.onSurface
            )
        }
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onMinimize() },
                    modifier = Modifier.padding(5.dp, 0.dp).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Minimize, "Minimize", tint = MaterialTheme.colors.onSurface)
                }
                IconButton(
                    onClick = { onMaximize(windowPlacement != WindowPlacement.Maximized) },
                    modifier = Modifier.padding(5.dp, 0.dp).size(24.dp)
                ) {
                    Icon(
                        if (windowPlacement == WindowPlacement.Floating) {
                            Icons.Outlined.OpenInFull
                        } else {
                            Icons.Outlined.CloseFullscreen
                        },
                        "Maximize",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
                IconButton(
                    onClick = { onCloseApp() },
                    modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Close, "Close", tint = MaterialTheme.colors.onSurface)
                }
            }

        }
    }
}
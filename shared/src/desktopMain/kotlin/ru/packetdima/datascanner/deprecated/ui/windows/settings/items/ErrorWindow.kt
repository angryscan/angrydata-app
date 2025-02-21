package ru.packetdima.datascanner.deprecated.ui.windows.settings.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.close

@Composable
fun ErrorWindow(
    title: String,
    text: String,
    onCloseRequest: () -> Unit,
    state: DialogState = rememberDialogState(size = DpSize(450.dp, 200.dp))
) {
    DialogWindow(
        onCloseRequest = {},
        transparent = true,
        undecorated = true,
        state = state
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colors.background,
            modifier = Modifier
                .border(
                    BorderStroke(1.dp, MaterialTheme.colors.error.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.W600,
                        fontSize = 16.sp
                    )
                }
                Divider(Modifier.fillMaxWidth(), color = MaterialTheme.colors.error)

                Column(
                    modifier = Modifier
                        .padding(12.dp),

                ) {
                    Text(
                        text = text,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 14.sp,
                    )
                    Spacer(
                        Modifier.weight(1f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            onClick = onCloseRequest,
                        ) {
                            Text(text = stringResource(Res.string.close), color = MaterialTheme.colors.error)
                        }
                    }

                }

            }
        }
    }
}
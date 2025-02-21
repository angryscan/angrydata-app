package ru.packetdima.datascanner.deprecated.ui.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.rememberDialogState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.confirmButton
import ru.packetdima.datascanner.resources.no
import ru.packetdima.datascanner.resources.yes

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onCancel: () -> Unit,
    onAccept: () -> Unit,
    confirmButtonColor: Color = MaterialTheme.colors.primary,
    declineButtonColor: Color = MaterialTheme.colors.primary,
    confirmButtonText: String = stringResource(Res.string.yes),
    declineButtonText: String = stringResource(Res.string.no)
) {
    DialogWindow(onCloseRequest = {
        onCancel()
    },
        state = rememberDialogState(width = 400.dp, height = 160.dp),
        title = title,
        undecorated = true,
        transparent = true,
        resizable = false,
        content = {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colors.primary), shape = RoundedCornerShape(4.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = MaterialTheme.colors.surface),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    DialogWindowTitleBar(title)
                    Text(
                        text = text,
                        modifier = Modifier.height(60.dp).padding(10.dp, 0.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                onAccept()
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent,
                                contentColor = confirmButtonColor
                            ),
                            border = BorderStroke(1.dp, confirmButtonColor),
                            modifier = Modifier.testTag("confirm_button")
                        ) { Text(text = confirmButtonText) }
                        Spacer(modifier = Modifier.width(180.dp))
                        OutlinedButton(
                            onClick = {
                                onCancel()
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent,
                                contentColor = declineButtonColor
                            ),
                            border = BorderStroke(1.dp, declineButtonColor),
                            modifier = Modifier.testTag("decline_button")
                        ) { Text(text = declineButtonText) }
                    }
                }
            }
        })
}
@OptIn(ExperimentalResourceApi::class)
@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    confirmButtonColor: Color = MaterialTheme.colors.primary,
    confirmButtonText: String = stringResource(Res.string.confirmButton),
) {
    DialogWindow(onCloseRequest = {
        onConfirm()
    },
        state = rememberDialogState(width = 400.dp, height = 160.dp),
        title = title,
        undecorated = true,
        transparent = true,
        resizable = false,
        content = {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colors.primary), shape = RoundedCornerShape(4.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = MaterialTheme.colors.surface),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    DialogWindowTitleBar(title)
                    Text(
                        text = text,
                        modifier = Modifier.height(60.dp).padding(10.dp, 0.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                onConfirm()
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent,
                                contentColor = confirmButtonColor
                            ),
                            border = BorderStroke(1.dp, confirmButtonColor),
                            modifier = Modifier.testTag("confirm_button")
                        ) { Text(text = confirmButtonText) }
                    }
                }
            }
        })
}

@Composable
fun DialogWindowScope.DialogWindowTitleBar(
    title: String
) {
        WindowDraggableArea {
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text= title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
}
package ru.packetdima.datascanner.ui.windows.settings.items

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.UserSignature
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.ui.UIProperties
import ru.packetdima.datascanner.ui.theme.CustomTheme

@Composable
fun UserSignatureWindow(
    onCloseRequest: () -> Unit,
    dialogState: DialogState = rememberDialogState(),
    theme: UIProperties.ThemeType,
    signatureName: String,
    allUserSignatures: MutableList<UserSignature>,
    onSave: (UserSignature) -> Unit
) {
    var name by remember { mutableStateOf(signatureName) }
    var sigText by remember { mutableStateOf("") }
    val signatures = remember { mutableStateListOf<String>() }

    var errorWindowVisible by remember { mutableStateOf(false) }
    val errorWindowTitle = stringResource(Res.string.errorUserSignatureTitle)
    var errorWindowText by remember { mutableStateOf("") }

    LaunchedEffect(signatureName, allUserSignatures) {
        signatures.clear()
        sigText = ""
        if (signatureName.isNotEmpty()) {
            val sig = allUserSignatures.find { it.name == signatureName }
            if (sig != null) {
                signatures.addAll(sig.searchSignatures)
                name = sig.name
            }
        } else {
            name = ""
        }
    }

    if (errorWindowVisible) {
        ErrorWindow(
            title = errorWindowTitle,
            text = errorWindowText,
            onCloseRequest = { errorWindowVisible = false }
        )
    }

    DialogWindow(
        onCloseRequest = onCloseRequest,
        state = dialogState,
        visible = true,
        title = stringResource(Res.string.userSignatureTitle),
        resizable = false,
        undecorated = true,
        transparent = true
    ) {
        CustomTheme(
            when (theme) {
                UIProperties.ThemeType.System -> isSystemInDarkTheme()
                UIProperties.ThemeType.Dark -> true
                UIProperties.ThemeType.Light -> false
            }
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colors.background,
                modifier = Modifier
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colors.primary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    DialogTitleBar {
                        UserSignatureTitleBar()
                    }
                    Divider(Modifier.fillMaxWidth(), color = MaterialTheme.colors.primary.copy(alpha = 0.5f))
                    Column(
                        Modifier.fillMaxSize()
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it.replace("""[^a-zA-Zа-яА-Я0-9_\s]+""".toRegex(), "")
                            },
                            label = { Text(stringResource(Res.string.name)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            singleLine = true,
                            enabled = signatureName.isEmpty()
                        )
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = sigText,
                                onValueChange = {
                                    sigText = it
                                },
                                label = { Text(stringResource(Res.string.signature)) },
                                modifier = Modifier
                                    .weight(0.85f)
                                    .onPreviewKeyEvent {
                                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                            if (!signatures.contains(sigText)) {
                                                signatures.add(sigText)
                                            }
                                            sigText = ""
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (!signatures.contains(sigText)) {
                                        signatures.add(sigText)
                                    }
                                    sigText = ""
                                },
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = null, tint = MaterialTheme.colors.primary)
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                            ) {
                                val lazyState = rememberLazyListState()

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(end = 12.dp),
                                    state = lazyState
                                ) {
                                    items(signatures) { sig ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                                .padding(vertical = 5.dp, horizontal = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = sig,
                                            )
                                            IconButton(
                                                modifier = Modifier.size(24.dp),
                                                onClick = { signatures.remove(sig) }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Delete,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colors.error
                                                )
                                            }
                                        }

                                    }
                                }

                                VerticalScrollbar(
                                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                    adapter = rememberScrollbarAdapter(
                                        scrollState = lazyState
                                    )
                                )
                            }
                        }


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (name.isEmpty()) {
                                        runBlocking {
                                            errorWindowText = getString(Res.string.errorUserSignatureNameEmpty)
                                            errorWindowVisible = true
                                        }
                                    } else {
                                        if (signatures.isEmpty()) {
                                            runBlocking {
                                                errorWindowText = getString(Res.string.errorUserSignatureEmpty)
                                                errorWindowVisible = true
                                            }
                                        } else {
                                            if (signatureName.isEmpty()) {
                                                if (allUserSignatures.find { it.name == name } == null) {
                                                    onSave(
                                                        UserSignature(
                                                            name = name.trim().replace("""\s+""".toRegex(), "_"),
                                                            writeName = name.trim(),
                                                            searchSignatures = signatures.toMutableList()
                                                        )
                                                    )
                                                } else {
                                                    runBlocking {
                                                        errorWindowText =
                                                            getString(Res.string.errorUserSignatureAlreadyExist)
                                                        errorWindowVisible = true
                                                    }
                                                }
                                            } else {
                                                val sig = allUserSignatures.find { it.name == name }
                                                if (sig != null) {
                                                    sig.searchSignatures = signatures
                                                    sig.name = name.replace("""\s+""".toRegex(), "_")
                                                    sig.writeName = name.trim()
                                                    onSave(sig)
                                                } else {
                                                    runBlocking {
                                                        errorWindowText = getString(Res.string.errorUserSignatureSave)
                                                        errorWindowVisible = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Transparent,
                                    contentColor = MaterialTheme.colors.primary
                                ),
                                border = null
                            ) {
                                Text(stringResource(Res.string.save).uppercase(), fontWeight = FontWeight.W600)
                            }
                            OutlinedButton(
                                onClick = {
                                    onCloseRequest()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Transparent,
                                    contentColor = MaterialTheme.colors.error
                                ),
                                border = null
                            ) {
                                Text(stringResource(Res.string.close).uppercase(), fontWeight = FontWeight.W600)
                            }
                        }
                    }
                }
            }
        }
    }
}
package ru.packetdima.datascanner.ui.windows.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.DetectFunction
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.searcher.FileType
import ru.packetdima.datascanner.store.ContextMenu
import ru.packetdima.datascanner.ui.CheckboxWithText
import ru.packetdima.datascanner.ui.custom.ConfirmationDialog
import ru.packetdima.datascanner.ui.strings.composableName
import ru.packetdima.datascanner.ui.windows.settings.items.LanguageSelection
import ru.packetdima.datascanner.ui.windows.settings.items.ThemeSelection
import ru.packetdima.datascanner.ui.windows.settings.items.ThreadCount
import ru.packetdima.datascanner.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SettingsWindow(
    onCloseClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    var coresCount by remember { mutableStateOf(Settings.searcher.threadCount.value) }

    var language by remember { mutableStateOf(Settings.ui.language.value) }
    var theme by remember { mutableStateOf(Settings.ui.theme.value) }

    val extensions by remember { mutableStateOf(Settings.searcher.extensions.toMutableList()) }
    val detectFunctions by remember { mutableStateOf(Settings.searcher.detectFunctions.toMutableList()) }

    var fastScan by remember { mutableStateOf(Settings.searcher.fastScan.value) }

    var confirmDialog by remember { mutableStateOf(false) }
    var contextMenu by remember { mutableStateOf(ContextMenu.enabled) }

    if (confirmDialog) {
        ConfirmationDialog(
            title = stringResource(Res.string.settingsChangedTitle),
            text = stringResource(Res.string.settingsChangedText),
            onConfirm = { onCloseClick() }
        )
    }

    Box(
        modifier = Modifier
            .size(300.dp, 460.dp)
            .background(colors.surface, RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colors.primary), shape = RoundedCornerShape(4.dp))
            .padding(10.dp, 10.dp, 0.dp, 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp)
        ) {
            Text(
                text = stringResource(Res.string.uiSettings),
                fontSize = 17.sp,
                fontWeight = FontWeight.W600,
                color = colors.onBackground
            )
            Column(
                modifier = Modifier.height(385.dp)
            ) {
                Column(
                    modifier = Modifier.verticalScroll(
                        state = scrollState
                    )
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    CheckboxWithText(
                        state = fastScan,
                        title = stringResource(Res.string.scanQuality),
                        text = stringResource(Res.string.fastScanCheckBox),
                        onStateChanged = {
                            fastScan = it
                        },
                        testTag = "fastScanCheckbox"
                    )

                    ThreadCount(
                        coresCount = coresCount,
                        onValueChanged = {
                            coresCount = it
                        }
                    )

                    LanguageSelection(
                        language = language,
                        onSelect = {
                            language = it
                        }
                    )

                    ThemeSelection(
                        theme = theme,
                        onSelect = {
                            theme = it
                        }
                    )

                    if(ContextMenu.supported()) {
                        CheckboxWithText(
                            state = contextMenu,
                            title = stringResource(Res.string.settingsContextMenuTitle),
                            text = stringResource(Res.string.settingsContextMenuText),
                            onStateChanged = {
                                contextMenu = it
                            },
                            testTag = "contextMenuCheckbox"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(Res.string.uiExtToScan),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W500,
                        color = colors.onSurface
                    )
                    ExtensionSelection(extensions)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(Res.string.uiDetectFun),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W500,
                        color = colors.onSurface
                    )
                    DetectFunctionSelection(detectFunctions)
                }


            }
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
            ) {
                OutlinedButton(
                    onClick = onCloseClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = colors.error
                    ),
                    border = null
                ) {
                    Text(stringResource(Res.string.close).uppercase(), fontWeight = FontWeight.W600)
                }
                OutlinedButton(
                    onClick = {
                        Settings.ui.theme.value = theme
                        if (Settings.ui.language.value != language) {
                            Settings.ui.language.value = language
                            confirmDialog = true
                        }
                        Settings.searcher.threadCount.value = coresCount
                        Settings.searcher.fastScan.value = fastScan
                        Settings.searcher.extensions.removeIf { !extensions.contains(it) }
                        Settings.searcher.extensions.addAll(
                            extensions.filter { !Settings.searcher.extensions.contains(it) }
                        )
                        Settings.searcher.detectFunctions.removeIf { !detectFunctions.contains(it) }
                        Settings.searcher.detectFunctions.addAll(
                            detectFunctions.filter { !Settings.searcher.detectFunctions.contains(it) }
                        )
                        Settings.searcher.save(AppFiles.SearchSettingsFile)

                        Settings.ui.save(AppFiles.UISettingsFile)

                        if (contextMenu != ContextMenu.enabled) {
                            ContextMenu.enabled = contextMenu
                        }

                        if (!confirmDialog)
                            onCloseClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = colors.primary
                    ),
                    border = null
                ) {
                    Text(stringResource(Res.string.save).uppercase(), fontWeight = FontWeight.W600)
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.padding(1.dp, 30.dp, 1.dp, 40.dp).width(8.dp)
        )
    }
}

@Composable
fun ExtensionSelection(extensions: MutableList<String>) {
    val allExtensions = FileType.entries
        .map { it.name }
    GroupedCheckbox(
        itemsList = allExtensions.map { Pair(Pair(it, it), extensions.contains(it)) },
        itemStateChanged = { item, state ->
            if (state && !extensions.contains(item))
                extensions.add(item as String)
            else if (!state)
                extensions.remove(item)
        },
        columnsCount = 3
    )
}

@Composable
fun DetectFunctionSelection(detectFunctions: MutableList<DetectFunction>) {
    val allFunctions =
        DetectFunction.entries.toTypedArray()
    GroupedCheckbox(
        itemsList = allFunctions.map {
            Pair(
                Pair(
                    it,
                    it.composableName()
                ), detectFunctions.contains(it)
            )
        },
        itemStateChanged = { item, state ->
            if (state && !detectFunctions.contains(item))
                detectFunctions.add(item as DetectFunction)
            else if (!state)
                detectFunctions.remove(item)
        },
        columnsCount = 1
    )
}

@Composable
fun GroupedCheckbox(
    itemsList: List<Pair<Pair<Any, String>, Boolean>>,
    itemStateChanged: (item: Any, state: Boolean) -> Unit,
    columnsCount: Int = 1
) {

    val columnList = (1..columnsCount).map { mutableListOf<Pair<Pair<Any, String>, Boolean>>() }
    itemsList.forEachIndexed { index, pair ->
        columnList[index % columnsCount].add(pair)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        columnList.forEach { column ->
            Column {
                column.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isChecked = remember { mutableStateOf(item.second) }

                        Checkbox(
                            checked = isChecked.value,
                            onCheckedChange = {
                                isChecked.value = it
                                itemStateChanged(item.first.first, it)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = colors.primary,
                                uncheckedColor = Color.DarkGray
                            ),
                            enabled = true,
                            modifier = Modifier.testTag("checkbox_${item.first.first}")
                        )
                        Text(text = item.first.second)
                    }
                }
            }
        }
    }
}
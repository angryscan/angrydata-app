package ru.packetdima.datascanner.deprecated.ui.screens.scanner

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.exposed.sql.SortOrder
import ru.packetdima.datascanner.scan.common.FilesCounter
import ru.packetdima.datascanner.searcher.SearcherResult
import ru.packetdima.datascanner.searcher.model.ResultRows
import ru.packetdima.datascanner.deprecated.ui.custom.AnimatedPopup
import ru.packetdima.datascanner.deprecated.ui.custom.ConfirmationDialog
import ru.packetdima.datascanner.ui.datatable.TableView
import ru.packetdima.datascanner.deprecated.ui.windows.ScanStatWindow
import ru.packetdima.datascanner.resources.*
import java.awt.Desktop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ResultScreen(
    filesCounter: FilesCounter,
    timeSpent: Int,
    onExported: () -> Unit,
    onComplete: () -> Unit
) {
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    val currentTime = formatter.format(time)

    var expandedStat by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    var downloadButtonColorState by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    var removeAllConfirmationVisible by remember { mutableStateOf(false) }

    var removeAllCompleateVisible by remember { mutableStateOf(false) }
    var deletedFilesCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3.seconds)
            downloadButtonColorState = !downloadButtonColorState
        }
    }

    val animatedDownloadButtonColor = animateColorAsState(
        targetValue = if (!downloadButtonColorState) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
        animationSpec = tween(1500, 0, FastOutLinearInEasing)
    )

    var updateTrigger by remember { mutableStateOf(false) }

    MaterialTheme {

        if(removeAllConfirmationVisible) {
            ConfirmationDialog(
                title = stringResource(Res.string.resultDeleteAllConfirmationTitle),
                text = stringResource(Res.string.resultDeleteAllConfirmationText),
                confirmButtonColor = MaterialTheme.colors.error,
                onCancel = {removeAllConfirmationVisible = false},
                onAccept = {
                    removeAllConfirmationVisible = false
                    deletedFilesCount = SearcherResult.deleteAllFiles()
                    removeAllCompleateVisible = true
                }
            )
        }
        if(removeAllCompleateVisible){
            ConfirmationDialog(
                title = stringResource(Res.string.resultDeleteConfirmationTitle),
                text = stringResource(Res.string.resultDeleteConfirmationText, deletedFilesCount),
                onCancel = {
                    removeAllCompleateVisible = false
                    onComplete()
                },
                onAccept = {
                    removeAllCompleateVisible = false
                    onComplete()
                },
                confirmButtonText = stringResource(Res.string.confirmButton),
                declineButtonText = stringResource(Res.string.close)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(15.dp, 50.dp, 15.dp, 50.dp)
                .testTag("result_screen"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                AnimatedPopup(
                    expanded = expandedStat,
                    density = density,
                    alignment = Alignment.TopStart,
                    onClose = { expandedStat = !expandedStat }
                ) {
                    ScanStatWindow(
                        filesCounter = filesCounter,
                        timeSpent = timeSpent,
                        onCloseClick = { expandedStat = !expandedStat }
                    )
                }
                OutlinedButton(
                    onClick = { expandedStat = !expandedStat },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.primary
                    ),
                    border = null
                ) {
                    Icon(Icons.Outlined.Equalizer, stringResource(Res.string.resultStatisticDesc), tint = MaterialTheme.colors.primary)
                    Text(stringResource(Res.string.resultStatisticsButton).uppercase(), color = MaterialTheme.colors.primary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            removeAllConfirmationVisible = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.primary
                        ),
                        border = null
                    ) {
                        Icon(Icons.Outlined.DeleteOutline, stringResource(Res.string.resultDeleteAllButtonDesk), tint = MaterialTheme.colors.error)
                        Text(stringResource(Res.string.resultDeleteAllButton).uppercase(), color = MaterialTheme.colors.error)

                    }
                    OutlinedButton(
                        onClick = {
                            if (SearcherResult.saveResult("ADS_$currentTime.csv"))
                                onExported()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.primary
                        ),
                        border = null
                    ) {
                        Icon(Icons.Outlined.FileDownload, stringResource(Res.string.download), tint = animatedDownloadButtonColor.value)
                        Text(stringResource(Res.string.download).uppercase(), color = animatedDownloadButtonColor.value)

                    }
                }
            }
            var expanded by remember { mutableStateOf(false) }
            var value by remember { mutableStateOf(null as Any?) }
            var filePath by remember { mutableStateOf("") }


            TableView(
                dataTable = ResultRows,
                onCellSecondaryClick = { row, column ->
                    expanded = true
                    value = row[column]
                    filePath = row[ResultRows::path].toString()
                },
                onCellPrimaryClick = { row, header ->
                    if(header.text == "File name"){
                        Desktop.getDesktop().open(File(row[header].toString()))
                    }
                },
                defaultSort = Pair(ResultRows.score, SortOrder.DESC),
                rowsLimit = 1000,
                updateTrigger = updateTrigger
            )
            CursorDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                DropdownMenuItem(
                    onClick = {
                        Desktop.getDesktop().open(File(filePath))
                        expanded = false
                    }
                ){
                    Text(stringResource(Res.string.openFile))
                }
                DropdownMenuItem(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(value.toString()))
                        expanded = false
                    }
                ) {
                    Text(stringResource(Res.string.copyPath))
                }
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        if(SearcherResult.deleteFile(filePath))
                            updateTrigger = !updateTrigger
                    }
                ){
                    Text(stringResource(Res.string.deleteFile), color = MaterialTheme.colors.error)
                }
            }
        }
    }
}
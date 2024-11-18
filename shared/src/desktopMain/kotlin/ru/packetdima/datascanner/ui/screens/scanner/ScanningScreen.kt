package ru.packetdima.datascanner.ui.screens.scanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.misc.FilesCounter
import ru.packetdima.datascanner.searcher.SearcherResult
import ru.packetdima.datascanner.searcher.SensitiveSearcher
import ru.packetdima.datascanner.searcher.Writer
import ru.packetdima.datascanner.ui.AnimatedProgressIndicator
import ru.packetdima.datascanner.ui.custom.ConfirmationDialog
import ru.packetdima.datascanner.resources.*
import java.io.File
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ScanningScreen(
    directory: File,
    onCancel: () -> Unit,
    onComplete: (
        filesCounter: FilesCounter,
        timeSpent: Int
    ) -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    var started by remember { mutableStateOf(false) }
    var canceled by remember { mutableStateOf(false) }
    var isAskingToResult by remember { mutableStateOf(false) }

    val filesCounter by remember { mutableStateOf(FilesCounter()) }

    var ticks by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    var job: Job? by remember { mutableStateOf(null) }

    var generatingReport by remember { mutableStateOf(false) }
    var scanStarted by remember { mutableStateOf(false) }

    MaterialTheme {
        if (isAskingToResult) {
            ConfirmationDialog(
                title = stringResource(Res.string.scanCancelDialog),
                text = stringResource(Res.string.scanCancelShow),
                onCancel = {
                    isAskingToResult = false
                    onCancel()
                },
                onAccept = {
                    isAskingToResult = false
                    SearcherResult.createResult()
                    onComplete(
                        filesCounter,
                        ticks
                    )
                }
            )
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
            .testTag("scanning_screen")
    ) {
        if (!started && !canceled) {
            try {

                started = true

                runBlocking {
                    Writer.initDB()
                }

                job = coroutineScope.launch {

                    withContext(Dispatchers.IO) {
                        val searcher = SensitiveSearcher()

                        searcher.inspectDirectory(
                            directory = directory,
                            onProgressChange = {
                                if (it.first >= 0) {
                                    scanStarted = true
                                    filesCounter.scannedFilesCount = it.first
                                    filesCounter.scannedFilesSize = it.second
                                    progress =
                                        (filesCounter.scannedFilesCount + filesCounter.skippedScanFilesCount).toFloat() / filesCounter.selectedFilesCount
                                } else {
                                    scanStarted = false
                                    generatingReport = true
                                }
                            },
                            onFileFound = {
                                filesCounter.selectedFilesCount++
                                filesCounter.selectedFilesSize += it

                                filesCounter.totalFilesCount++
                                filesCounter.totalFilesSize += it
                            },
                            onSkipSelectFile = {
                                filesCounter.totalFilesSize += it
                                filesCounter.totalFilesCount++
                            },
                            onSkipScanFile = {
                                filesCounter.skippedScanFilesCount++
                                filesCounter.skippedScanFilesSize += it
                                progress =
                                    (filesCounter.scannedFilesCount + filesCounter.skippedScanFilesCount).toFloat() / filesCounter.selectedFilesCount
                            },
                            onReportCreated = {
                                filesCounter.valuebleFilesCount = it.first
                                filesCounter.valuebleFilesSize = it.second
                            }
                        )
                    }
                }
                job?.invokeOnCompletion {
                    if (!canceled)
                        onComplete(
                            filesCounter,
                            ticks
                        )
                    else
                        isAskingToResult = true
                }
            } catch (e: Exception) {
                logger.error { e.message }
                logger.error { e.stackTraceToString() }
                throw (e)
            }
        }
        LaunchedEffect(Unit) {
            while (started) {
                delay(1.seconds)
                ticks++
            }
        }
        Text(
            text = if (generatingReport) {
                stringResource(Res.string.scanReportGenerating)
            } else {
                if (!canceled) {
                    if (scanStarted)
                        stringResource(
                            Res.string.scanScanning,
                            (filesCounter.scannedFilesCount + filesCounter.skippedScanFilesCount),
                            filesCounter.selectedFilesCount
                        )
                    else
                        stringResource(
                            Res.string.scanSelecting,
                            filesCounter.selectedFilesCount,
                            filesCounter.totalFilesCount
                        )
                } else {
                    stringResource(Res.string.scanCancelling)
                }
            },
            modifier = Modifier.padding(4.dp)
        )
        if (!scanStarted || canceled) {
            LinearProgressIndicator(
                modifier = Modifier.width(550.dp),
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.4f),
                color = MaterialTheme.colors.primary
            )
        } else {
            AnimatedProgressIndicator(
                indicatorProgress = progress,
                modifier = Modifier.width(550.dp),
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.4f),
                color = MaterialTheme.colors.primary
            )
        }
        Text(
            text = "${(ticks / 3600).toString().padStart(2, '0')}:${
                ((ticks / 60) % 60).toString().padStart(2, '0')
            }:${(ticks % 60).toString().padStart(2, '0')}",
            modifier = Modifier.padding(0.dp, 1.dp, 0.dp, 0.dp)
        )
        OutlinedButton(
            onClick = {
                canceled = true
                started = false
                job?.cancel()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = MaterialTheme.colors.onSurface
            ),
            border = null
        ) {
            Text(
                text = stringResource(Res.string.stop).uppercase(),
                fontWeight = FontWeight.Thin,
                color = LocalContentColor.current.copy(alpha = 0.7f),
                modifier = Modifier.testTag("confirmation_button")
            )
        }
    }
}

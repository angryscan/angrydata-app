package ru.packetdima.datascanner.ui.windows

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.misc.FilesCounter
import ru.packetdima.datascanner.ui.windows.statistic.StatText
import ru.packetdima.datascanner.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ScanStatWindow(
    filesCounter: FilesCounter,
    timeSpent: Int,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(400.dp, 240.dp)
            .background(MaterialTheme.colors.surface, RoundedCornerShape(4.dp))
            .border(BorderStroke(1.dp, MaterialTheme.colors.primary), shape = RoundedCornerShape(4.dp))
            .padding(10.dp)
            .testTag("scan_stat_window")
    ) {
        Column {
            Text(
                text = stringResource(
                    Res.string.statTime,
                    (timeSpent / 3600).toString().padStart(2, '0'),
                    ((timeSpent / 60) % 60).toString().padStart(2, '0'),
                    (timeSpent % 60).toString().padStart(2, '0')
                ),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(0.dp, 10.dp, 2.dp, 0.dp)
            )
            StatText(
                resourceString = Res.string.statTotalFile,
                count = filesCounter.totalFilesCount,
                fileSize = filesCounter.totalFilesSize
            )
            StatText(
                resourceString = Res.string.statFilesSelected,
                count = filesCounter.selectedFilesCount,
                fileSize = filesCounter.selectedFilesSize
            )
            StatText(
                resourceString = Res.string.statFilesScanned,
                count = filesCounter.scannedFilesCount,
                fileSize = filesCounter.scannedFilesSize
            )
            StatText(
                resourceString = Res.string.statFilesSkipped,
                count = filesCounter.skippedScanFilesCount,
                fileSize = filesCounter.skippedScanFilesSize
            )
            StatText(
                resourceString = Res.string.statFilesValuable,
                count = filesCounter.valuebleFilesCount,
                fileSize = filesCounter.valuebleFilesSize
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
            ) {
                OutlinedButton(
                    onClick = onCloseClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.error
                    ),
                    modifier = Modifier.testTag("close_button"),
                    border = null
                ) {
                    Text(stringResource(Res.string.close).uppercase(), fontWeight = FontWeight.W600)
                }
            }
        }
    }
}
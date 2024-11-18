package ru.packetdima.datascanner.ui.windows.statistic

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.misc.FileSize

@OptIn(ExperimentalResourceApi::class)
@Composable
fun StatText(
    resourceString: StringResource,
    count: Long,
    fileSize: FileSize
) {
    Text(
        text = stringResource(
            resourceString,
            count,
            fileSize.toString()
        ),
        color = MaterialTheme.colors.onSurface,
        modifier = Modifier.padding(0.dp, 10.dp, 2.dp, 0.dp)
    )
}
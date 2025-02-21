package ru.packetdima.datascanner.deprecated.ui.windows.settings.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.uiThreadCount

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ThreadCount(coresCount: Int, onValueChanged: (Int) -> Unit) {
    val maxCores = Runtime.getRuntime().availableProcessors()
    Text(
        text = stringResource(Res.string.uiThreadCount),
        fontSize = 17.sp,
        fontWeight = FontWeight.W500,
        color = MaterialTheme.colors.onSurface
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Slider(
            value = coresCount.toFloat(),
            valueRange = 1f..maxCores.toFloat(),
            onValueChange = { onValueChanged(it.toInt()) },
            steps = maxCores - 2,
            modifier = Modifier.width(240.dp)
                .testTag("thread_count_slider")
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            "$coresCount",
            fontSize = 14.sp,
            modifier = Modifier.testTag("thread_count_text")
        )
    }
}
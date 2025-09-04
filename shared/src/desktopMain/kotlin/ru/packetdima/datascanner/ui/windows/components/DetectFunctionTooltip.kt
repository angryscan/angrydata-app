package ru.packetdima.datascanner.ui.windows.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import info.downdetector.bigdatascanner.common.IDetectFunction
import ru.packetdima.datascanner.ui.strings.description

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetectFunctionTooltip(detectFunction: IDetectFunction, block: @Composable () -> Unit) {
    DescriptionTooltip(
        description = detectFunction.description(),
        block = block
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DescriptionTooltip(description: String, delay: Int = 500, block: @Composable () -> Unit) {
    TooltipArea(
        tooltip = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraSmall,
                tonalElevation = 10.dp,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = description,
                    modifier = Modifier.padding(8.dp),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        },
        delayMillis = delay
    ) {
        block()
    }
}
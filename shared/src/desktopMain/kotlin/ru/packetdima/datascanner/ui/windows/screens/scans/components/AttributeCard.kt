package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.downdetector.bigdatascanner.common.IDetectFunction
import ru.packetdima.datascanner.ui.strings.composableName
import ru.packetdima.datascanner.ui.windows.components.DetectFunctionTooltip

@Composable
fun AttributeCard(attribute: IDetectFunction) {
    DetectFunctionTooltip(
        detectFunction = attribute
    ) {
        Box(
            modifier = Modifier
                .clip(
                    MaterialTheme.shapes.small
                )
                .background(color = MaterialTheme.colorScheme.secondary)
                .padding(4.dp)
        ) {
            Text(
                text = attribute.composableName(),
                fontSize = 14.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.1.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
fun AttributeCard(attribute: IDetectFunction, onClick: () -> Unit, enabled: Boolean) {
    DetectFunctionTooltip(
        detectFunction = attribute
    ) {
        Box(
            modifier = Modifier
                .clip(
                    MaterialTheme.shapes.small
                )
                .background(
                    color = if (enabled)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.outlineVariant
                )
                .clickable(
                    onClick = onClick,
                    enabled = enabled
                )
                .padding(4.dp)
        ) {
            Text(
                text = attribute.composableName(),
                fontSize = 14.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.1.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}
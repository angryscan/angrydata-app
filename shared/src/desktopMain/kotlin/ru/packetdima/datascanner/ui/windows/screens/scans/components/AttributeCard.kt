package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.downdetector.bigdatascanner.common.DetectFunction
import info.downdetector.bigdatascanner.common.IDetectFunction
import ru.packetdima.datascanner.ui.strings.composableName

@Composable
fun AttributeCard(attribute: IDetectFunction) {
    Box(
        modifier = Modifier
            .clip(
                MaterialTheme.shapes.small
            )
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(4.dp)
    ) {
        Text(
            text = if (attribute is DetectFunction) attribute.composableName() else attribute.writeName,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.1.sp,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}
package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AttributeFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    tint: Color = LocalContentColor.current
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .height(24.dp),
        label = {
            Text(
                text = text,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.1.sp,
            )
        },
        leadingIcon = {
            if(selected) {
                Icon(
                    imageVector = Icons.Outlined.Done,
                    contentDescription = null,
                    tint = tint
                )
            }
        },
    )
}
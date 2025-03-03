package ru.packetdima.datascanner.ui.windows.screens.scans.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ScanFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text) },
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Outlined.Done,
                    contentDescription = null,
                    modifier = Modifier
                        .size(FilterChipDefaults.IconSize)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(FilterChipDefaults.IconSize)
                )
            }
        }
    )
}
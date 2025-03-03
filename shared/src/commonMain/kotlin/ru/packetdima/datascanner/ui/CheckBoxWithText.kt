package ru.packetdima.datascanner.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.onClick
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckboxWithText(state: Boolean, title: String, text: String, onStateChanged: (Boolean) -> Unit, testTag: String) {
    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.W500,
        color = colorScheme.onSurface
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = {
                onStateChanged(!state)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = colorScheme.primary,
                uncheckedColor = Color.DarkGray
            ),
            enabled = true,
            modifier = Modifier.testTag(testTag)
        )
        Text(
            text = text,
            modifier = Modifier.onClick {
                onStateChanged(!state)
            }
        )
    }
}
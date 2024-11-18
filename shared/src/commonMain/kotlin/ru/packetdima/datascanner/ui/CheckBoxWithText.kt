package ru.packetdima.datascanner.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.onClick
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun CheckboxWithText(state: Boolean, title: String, text: String, onStateChanged: (Boolean) -> Unit, testTag: String) {
    Text(
        text = title,
        fontSize = 17.sp,
        fontWeight = FontWeight.W500,
        color = colors.onSurface
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
                checkedColor = colors.primary,
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
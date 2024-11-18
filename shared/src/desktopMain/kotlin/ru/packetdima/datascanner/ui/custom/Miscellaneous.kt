package ru.packetdima.datascanner.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomOutlinedTextBox(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.onSurface,
    borderColor: Color
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
            .clip(shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp))
            .border(0.5.dp, borderColor, shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp))
            .padding(16.dp, 10.dp, 10.dp, 10.dp),

        textStyle = TextStyle(
            color = textColor,
            fontSize = 16.sp
        ),
        decorationBox = { innerTextField ->
            Row(modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) Text(placeholder, color = textColor.copy(alpha = 0.65f))
            }
            innerTextField()
        }
    )
}

@Composable
fun DropdownMenu(
    expanded: Boolean,
    selectedIndex: Int,
    items: List<String>,
    onSelect: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Box {
        content()
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            items.forEachIndexed { index, s ->
                if (selectedIndex == index) {
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colors.primary,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        onClick = { onSelect(index) }
                    ) {
                        Text(
                            text = s,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSelect(index) }
                    ) {
                        Text(
                            text = s,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
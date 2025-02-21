package ru.packetdima.datascanner.deprecated.ui.windows.settings.items

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.ui.UIProperties
import ru.packetdima.datascanner.ui.custom.DropdownMenu
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.uiTheme

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ThemeSelection(
    theme: UIProperties.ThemeType,
    onSelect: (UIProperties.ThemeType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Text(
        text = stringResource(Res.string.uiTheme),
        fontSize = 17.sp,
        fontWeight = FontWeight.W500,
        color = MaterialTheme.colors.onSurface
    )
    DropdownMenu(
        expanded = expanded,
        selectedIndex = theme.ordinal,
        items = UIProperties.ThemeType.entries.map { it.toString() },
        onSelect = { index ->
            onSelect(UIProperties.ThemeType.entries.first { it.ordinal == index })
            expanded = false
        },
        onDismissRequest = {
            expanded = false
        }
    ) {
        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(1f)
                .testTag("theme_button")
        ) {

            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = null)
            Text(
                text = theme.toString(),
                modifier = Modifier.fillMaxWidth(1f)
                    .testTag("theme_text")
            )
        }
    }
}
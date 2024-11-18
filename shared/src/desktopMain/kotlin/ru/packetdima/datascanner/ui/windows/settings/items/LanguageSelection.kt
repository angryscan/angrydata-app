package ru.packetdima.datascanner.ui.windows.settings.items

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
import ru.packetdima.datascanner.resources.uiLanguage

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LanguageSelection(
    language: UIProperties.LanguageType,
    onSelect: (UIProperties.LanguageType) -> Unit
) {
    var expanded by remember { mutableStateOf(false)}
    Text(
        text = stringResource(Res.string.uiLanguage),
        fontSize = 17.sp,
        fontWeight = FontWeight.W500,
        color = MaterialTheme.colors.onSurface
    )
    DropdownMenu(
        expanded = expanded,
        selectedIndex = language.ordinal,
        items = UIProperties.LanguageType.entries.map { it.text },
        onSelect = { index ->
            onSelect(UIProperties.LanguageType.entries.first { it.ordinal == index })
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
                .testTag("language_button")
        ) {

            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = null)
            Text(
                text = language.text,
                modifier = Modifier.fillMaxWidth(1f)
                    .testTag("language_text")
            )
        }
    }
}
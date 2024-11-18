package ru.packetdima.datascanner.custom

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.use
import ru.packetdima.datascanner.ui.custom.CustomOutlinedTextBox
import ru.packetdima.datascanner.ui.custom.DropdownMenu
import kotlin.test.Test

internal class MiscellaneousKtTest {

    @Test
    fun customOutlinedTextBox() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                CustomOutlinedTextBox(
                    placeholder = "",
                    value = "",
                    onValueChange = {},
                    borderColor = Color.Black
                )
            }
        }
    }

    @Test
    fun dropdownMenu() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                DropdownMenu(
                    expanded = true,
                    selectedIndex = 0,
                    items = listOf("a", "b"),
                    onSelect = {},
                    onDismissRequest = {},
                    content = {}
                )
            }
        }
    }
}
package ru.packetdima.datascanner.items

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.use
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.ui.windows.settings.DetectFunctionSelection
import ru.packetdima.datascanner.ui.windows.settings.ExtensionSelection
import ru.packetdima.datascanner.ui.windows.settings.GroupedCheckbox
import ru.packetdima.datascanner.ui.windows.settings.SettingsWindow
import kotlin.test.Test

internal class SettingsWindowKtTest: InitSettingsTest {

    @Test
    fun settingsBox() {
        initSettings()

        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                SettingsWindow(
                    onCloseClick = {}
                )
            }
        }
    }

    @Test
    fun extensionSelection() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                ExtensionSelection(
                    mutableListOf()
                )
            }
        }
    }

    @Test
    fun detectFunctionSelection() {
        initSettings()

        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                DetectFunctionSelection(
                    mutableListOf()
                )
            }
        }
    }

    @Test
    fun groupedCheckbox() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                GroupedCheckbox(
                    listOf(Pair(Pair("",""), true)),
                    itemStateChanged = { _, _ -> }
                )
                GroupedCheckbox(
                    listOf(Pair(Pair("",""), true)),
                    itemStateChanged = { _, _ -> },
                    columnsCount = 1
                )
            }
        }
    }
}
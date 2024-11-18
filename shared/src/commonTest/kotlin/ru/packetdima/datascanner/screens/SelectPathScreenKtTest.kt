package ru.packetdima.datascanner.screens

import androidx.compose.ui.test.*
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.ui.screens.scanner.SelectPathScreen
import kotlin.test.Test

internal class SelectPathScreenKtTest : InitSettingsTest {
    init {
        initSettings()
    }
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Test select path screen`() = runComposeUiTest{
        setContent {
            SelectPathScreen(
                onStart = {},
                onPathSelectionChanged = {}
            )
        }

        onNodeWithTag("select_path_screen").assertExists()

        //rule.onNodeWithTag("open_path_button").performClick() // можем открыть, но не можем закрыть, т.к. не можем управлять отсюда объектом JFileChooser
        //поэтому вводим адрес непосредственно в текстовое поле:
        val path = javaClass.getResource("/files")
        if (path != null) {
            onNodeWithTag("select_path_input").performTextInput(path.file)
        }
        onNodeWithTag("scan_start_button").performClick() // т.к. onStart не задано, проверить дальше не можем
    }
}
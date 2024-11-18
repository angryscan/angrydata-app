package ru.packetdima.datascanner.screens

import ru.packetdima.datascanner.ui.screens.scanner.MainWindowScreen
import kotlin.test.Test
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.*
import ru.packetdima.datascanner.common.InitSettingsTest

internal class MainWindowScreenKtTest : InitSettingsTest {
    init {
        initSettings()
    }
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Test scanning screen`() = runComposeUiTest {
        setContent {
            MainWindowScreen()
        }

        onNodeWithTag("toggle_select_path").performClick()
        onNodeWithTag("select_path_screen").assertExists()

        onNodeWithTag("toggle_scanning").performClick()
//      onNodeWithTag("scanning_screen").assertExists()

        onNodeWithTag("toggle_result").performClick()
//      onNodeWithTag("result_screen").assertExists()
    }
}
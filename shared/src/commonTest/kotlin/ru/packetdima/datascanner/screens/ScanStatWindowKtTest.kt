package ru.packetdima.datascanner.screens

import androidx.compose.ui.test.*
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.scan.common.FilesCounter
import ru.packetdima.datascanner.ui.windows.ScanStatWindow
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ScanStatWindowKtTest : InitSettingsTest {
    init {
        initSettings()
    }
    @Test
    fun `Test open&close scan stat window`() = runComposeUiTest{
        setContent {
            ScanStatWindow(FilesCounter(), 1, {})
        }
        onNodeWithTag("scan_stat_window").assertExists()
        onNodeWithTag("close_button").performClick()
    }
}
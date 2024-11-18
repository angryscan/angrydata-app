package ru.packetdima.datascanner.screens

import androidx.compose.ui.test.*
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.ui.windows.AppInfoWindow
import kotlin.test.Test

class AppInfoKtTest : InitSettingsTest {
    init {
        initSettings()
    }
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Test app info window`() = runComposeUiTest {
        setContent {
            AppInfoWindow({})
        }
        onNodeWithTag("appinfo_window").assertExists()
        onNodeWithTag("close_appinfo_button").performClick()
    }
}
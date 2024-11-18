package ru.packetdima.datascanner.items

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.use
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.ui.windows.AppInfoWindow
import ru.packetdima.datascanner.ui.windows.getAppVersion
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AppInfoWindowKtTest: InitSettingsTest {

    @Test
    fun getAppVersionTest() {
        assertEquals("Debug", getAppVersion())
    }

    @Test
    fun infoBox() {
        initSettings()
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                AppInfoWindow { }
            }
        }
    }
}
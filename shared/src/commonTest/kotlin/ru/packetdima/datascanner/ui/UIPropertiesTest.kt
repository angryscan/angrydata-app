package ru.packetdima.datascanner.ui

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class UIPropertiesTest {

    @Test
    fun test() {
        val uiPath = javaClass.getResource("/common/ui.json")
        assertNotNull(uiPath)
        val ui = UIProperties(uiPath.file)
        ui.theme.value = UIProperties.ThemeType.System
        assertEquals(UIProperties.ThemeType.System, ui.theme.value)
        ui.theme.value = UIProperties.ThemeType.Light
        assertEquals(UIProperties.ThemeType.Light, ui.theme.value)
        ui.theme.value = UIProperties.ThemeType.Dark
        assertEquals(UIProperties.ThemeType.Dark, ui.theme.value)
        val tmpFile = File.createTempFile("ads_ui", ".json")
        ui.save(tmpFile)
        tmpFile.delete()
    }
}
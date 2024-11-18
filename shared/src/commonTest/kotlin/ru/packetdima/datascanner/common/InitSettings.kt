package ru.packetdima.datascanner.common

import ru.packetdima.datascanner.searcher.properties.Properties
import ru.packetdima.datascanner.ui.UIProperties
import kotlin.test.assertNotNull

interface InitSettingsTest {
    fun initSettings() {
        val uiPath = javaClass.getResource("/common/ui.json")
        val searcherPath = javaClass.getResource("/common/properties.json")
        assertNotNull(uiPath)
        assertNotNull(searcherPath)
        Settings.ui = UIProperties(uiPath.file)
        Settings.searcher = Properties(searcherPath.file)
    }
}
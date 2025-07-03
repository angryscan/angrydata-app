package ru.packetdima.datascanner.types

import IKoinTestRule
import kotlinx.coroutines.runBlocking
import ru.packetdima.datascanner.scan.common.files.types.PPTType
import ru.packetdima.datascanner.searcher.Matrix
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PPTTypeTest: IKoinTestRule {
    @Test
    fun findLocation() {
        val filePath = javaClass.getResource("/files/first.ppt")?.file
        assertNotNull(filePath)

        val map = Matrix.getMap("first.ppt")
        assertNotNull(map)

        map.keys.forEach { df ->
            val locations = runBlocking {  PPTType.findLocation(filePath, df) }
            assertEquals(map[df], locations.size, "Wrong number of locations for ${df.name}")
            locations.forEach { location -> assertTrue(location.location.isNotEmpty(), "Wrong location for ${df.name} and entry ${location.entry}") }
        }
    }
}
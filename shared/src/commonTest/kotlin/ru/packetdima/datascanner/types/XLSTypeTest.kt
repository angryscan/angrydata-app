package ru.packetdima.datascanner.types

import IKoinTestRule
import kotlinx.coroutines.runBlocking
import ru.packetdima.datascanner.scan.common.files.types.XLSType
import ru.packetdima.datascanner.searcher.Matrix
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class XLSTypeTest : IKoinTestRule {

    @Test
    fun findLocation() {
        val filePath = javaClass.getResource("/files/first.xls")?.file
        assertNotNull(filePath)

        val map = Matrix.getMap("first.xls")
        assertNotNull(map)

        map.keys.forEach { df ->
            val locations = runBlocking { XLSType.findLocation(filePath, df) }
            assertEquals(map[df], locations.size, "Wrong number of locations for ${df.name}")
            locations.forEach { location ->
                assertTrue(
                    location.location.isNotEmpty(),
                    "Wrong location for ${df.name} and entry ${location.entry}"
                )
            }
        }
    }

}
package ru.packetdima.datascanner.types

import kotlinx.coroutines.runBlocking
import org.angryscan.common.engine.IMatcher
import org.angryscan.common.engine.hyperscan.HyperScanEngine
import ru.packetdima.datascanner.scan.common.files.Location
import ru.packetdima.datascanner.scan.engine.toHyperScanMatchers
import ru.packetdima.datascanner.searcher.Matrix
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

object CheckLocation {
    fun checkByMap(fileName: String, scanMethod: suspend (String, HyperScanEngine, IMatcher) -> List<Location>) {
        val map = Matrix.getMap(fileName.replace("/files/", ""))
        assertNotNull(map)

        val filePath = javaClass.getResource("/files/$fileName")?.file
        assertNotNull(filePath)

        val engine = HyperScanEngine(map.keys.toList().toHyperScanMatchers())

        map.keys.forEach { matcher ->
            val locations =
                runBlocking { scanMethod(filePath, engine, matcher) }//runBlocking {  DOCXType.findLocation(fileName, engine, matcher) }
            assertEquals(map[matcher], locations.size, "Wrong number of locations for ${matcher.name}")
            locations.forEach { location ->
                assertTrue(
                    location.location.isNotEmpty(),
                    "Wrong location for ${matcher.name} and entry ${location.entry}"
                )
            }
        }
    }
}
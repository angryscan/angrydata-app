package ru.packetdima.datascanner.searcher.properties

import java.io.File
import kotlin.test.Test

internal class PropertiesTest {

    @Test
    fun test() {
        val prop = javaClass.getResource("/common/properties.json")?.let { Properties(it.file) }
        val tmpFile = File.createTempFile("properties", ".json")
        prop?.save(tmpFile)
        tmpFile.delete()
        prop?.threadCount
        if(prop != null) {
            for (f in prop.detectFunctions)
                continue
            for (e in prop.extensions)
                continue
        }
    }
}
package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.Cleaner
import info.downdetector.bigdatascanner.common.IDetectFunction
import ru.packetdima.datascanner.scan.common.files.Location

interface IFileType {
    suspend fun findLocation(
        filePath: String,
        detectFunction: IDetectFunction,
        fastScan: Boolean = true
    ): List<Location>

    fun getEntries(text: String, detectFunction: IDetectFunction): List<String> {
        val cleanText = Cleaner.Companion.cleanText(text)
        return detectFunction.scan(cleanText).toList()
    }

    fun scan(text: String, detectFunctions: List<IDetectFunction>): Map<IDetectFunction, Int> {
        val cleanText = Cleaner.Companion.cleanText(text)
        return detectFunctions.mapNotNull { f ->
            f.scan(cleanText).count().takeIf { it > 0 }
                .let {
                    if(it != null) f to it
                    else null
                }
        }.toMap()
    }
}
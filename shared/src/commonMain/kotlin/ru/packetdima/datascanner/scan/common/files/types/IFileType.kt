package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.Cleaner
import info.downdetector.bigdatascanner.common.IDetectFunction
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.FileType.Companion.scanSettings
import ru.packetdima.datascanner.scan.common.files.Location
import java.io.File
import kotlin.coroutines.CoroutineContext

interface IFileType {
    suspend fun scanFile(
        file: File,
        context: CoroutineContext,
        detectFunctions: List<IDetectFunction>,
        fastScan: Boolean
    ): Document

    suspend fun findLocation(
        filePath: String,
        detectFunction: IDetectFunction,
        fastScan: Boolean = true
    ): List<Location>

    fun getEntries(text: String, detectFunction: IDetectFunction): List<String> {
        val cleanText = Cleaner.cleanText(text)
        return detectFunction.scan(cleanText).toList()
    }

    fun scan(text: String, detectFunctions: List<IDetectFunction>): Map<IDetectFunction, Int> {
        val cleanText = Cleaner.cleanText(text)
        return detectFunctions.mapNotNull { f ->
            f.scan(cleanText).count().takeIf { it > 0 }
                .let {
                    if(it != null) f to it
                    else null
                }
        }.toMap()
    }

    fun isSampleOverload(sample: Int, fastScan: Boolean): Boolean {
        return (fastScan && sample >= scanSettings.sampleCount)
    }
}
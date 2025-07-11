package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.Cleaner
import info.downdetector.bigdatascanner.common.IDetectFunction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.Location
import java.io.File
import kotlin.coroutines.CoroutineContext

interface IFileType: KoinComponent {
    suspend fun scanFile(
        file: File,
        context: CoroutineContext,
        detectFunctions: List<IDetectFunction>,
        fastScan: Boolean
    ): Document

    suspend fun findLocation(
        filePath: String,
        detectFunction: IDetectFunction,
        fastScan: Boolean = false
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
        val scanSettings: ScanSettings by inject()
        return (fastScan && sample >= scanSettings.sampleCount)
    }

    fun isSampleOverload(sample: Int, fastScan: Boolean, isActive: Boolean): Boolean {
        if(!isActive) return true
        return isSampleOverload(sample, fastScan)
    }

    fun isLengthOverload(length: Int): Boolean {
        val scanSettings: ScanSettings by inject()
        return (length >= scanSettings.sampleLength)
    }

    fun isLengthOverload(length: Int, isActive: Boolean): Boolean {
        if(!isActive) return true
        return (isLengthOverload(length))
    }
}
package ru.packetdima.datascanner.scan.common.files.types

import org.angryscan.common.engine.IMatcher
import org.angryscan.common.engine.IScanEngine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.Location
import java.io.File
import kotlin.coroutines.CoroutineContext

interface IFileType : KoinComponent {
    suspend fun scanFile(
        file: File,
        context: CoroutineContext,
        engines: List<IScanEngine>,
        fastScan: Boolean
    ): Document

    suspend fun findLocation(
        filePath: String,
        engine: IScanEngine,

        matcher: IMatcher,
        fastScan: Boolean = false
    ): List<Location>

//    fun getEntries(text: String, engine: IScanEngine, matcher: IMatcher): List<Match> {
//
//        return matcher.scan(text = text).toList()
//    }

//    fun scan(text: String, engine: IScanEngine): List<Match> {
//        return engine.scan(text)
//    }
    fun scan(text: String, engine: IScanEngine): Map<IMatcher, Int> {
        return engine
            .scan(text)
            .groupBy { it.matcher }
            .map { it.key to it.value.size }
            .toMap()
    }

    fun isSampleOverload(sample: Int, fastScan: Boolean): Boolean {
        val scanSettings: ScanSettings by inject()
        return (fastScan && sample >= scanSettings.sampleCount)
    }

    fun isSampleOverload(sample: Int, fastScan: Boolean, isActive: Boolean): Boolean {
        if (!isActive) return true
        return isSampleOverload(sample, fastScan)
    }

    fun isLengthOverload(length: Int): Boolean {
        val scanSettings: ScanSettings by inject()
        return (length >= scanSettings.sampleLength)
    }

    fun isLengthOverload(length: Int, isActive: Boolean): Boolean {
        if (!isActive) return true
        return (isLengthOverload(length))
    }
}
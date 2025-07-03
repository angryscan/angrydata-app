package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.apache.poi.hwpf.HWPFOldDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.FileType
import ru.packetdima.datascanner.scan.common.files.FileType.Companion.scanSettings
import ru.packetdima.datascanner.scan.common.files.Location
import ru.packetdima.datascanner.scan.common.files.LocationFinder.ScanException
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.text.forEach

object DOCType: IFileType {
    override suspend fun scanFile(
        file: File,
        context: CoroutineContext,
        detectFunctions: List<IDetectFunction>,
        fastScan: Boolean
    ): Document {
        val str = StringBuilder()
        val res = Document(file.length(), file.absolutePath)
        var sample = 0
        try {
            withContext(Dispatchers.IO) {
                FileInputStream(file).use { inputStream ->
                    WordExtractor(inputStream).use { wordExtractor ->
                        wordExtractor.text.forEach { c ->
                            str.append(c)
                            if (str.length >= scanSettings.sampleLength || !isActive) {
                                res + withContext(context) { scan(str.toString(), detectFunctions) }
                                str.clear()
                                sample++
                                if (FileType.Companion.isSampleOverload(sample, fastScan) || !isActive) return@withContext
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            try {
                withContext(Dispatchers.IO) {
                    POIFSFileSystem(file).use { inputStream ->
                        HWPFOldDocument(inputStream).use { hwpfOldDocument ->
                            hwpfOldDocument.documentText.forEach { c ->
                                str.append(c)
                                if (str.length >= scanSettings.sampleLength || !isActive) {
                                    res + withContext(context) { scan(str.toString(), detectFunctions) }
                                    str.clear()
                                    sample++
                                    if (FileType.Companion.isSampleOverload(sample, fastScan) || !isActive) return@withContext
                                }
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                res.skip()
                return res
            }
        }
        if (str.isNotEmpty() && !FileType.Companion.isSampleOverload(sample, fastScan)) {
            res + withContext(context) { scan(str.toString(), detectFunctions) }
        }
        return res
    }

    override suspend fun findLocation(
        filePath: String,
        detectFunction: IDetectFunction,
        fastScan: Boolean
    ): List<Location> {
        var length = 0
        var sample = 0
        val locations = mutableListOf<Location>()
        try {
            withContext(Dispatchers.IO) {
                val file = File(filePath)
                FileInputStream(file).use { inputStream ->
                    WordExtractor(inputStream).use { extractor ->
                        extractor.paragraphText.forEachIndexed { index, text ->
                            getEntries(text, detectFunction).forEach {
                                locations.add(Location(it, "Paragraph:$index"))
                            }
                            length += text.length
                            if (length >= FileType.Companion.scanSettings.sampleLength || !isActive) {
                                length = 0
                                sample++
                                if (isSampleOverload(sample, fastScan) || !isActive) return@withContext
                            }
                        }
                        extractor.commentsText.forEachIndexed { index, text ->
                            getEntries(text, detectFunction).forEach {
                                locations.add(Location(it, "Comment:$index"))
                            }
                            length += text.length
                            if (length >= FileType.Companion.scanSettings.sampleLength || !isActive) {
                                length = 0
                                sample++
                                if (isSampleOverload(sample, fastScan) || !isActive) return@withContext
                            }
                        }
                        extractor.footnoteText.forEachIndexed { index, text ->
                            getEntries(text, detectFunction).forEach {
                                locations.add(Location(it, "Footnote:$index"))
                            }
                            length += text.length
                            if (length >= FileType.Companion.scanSettings.sampleLength || !isActive) {
                                length = 0
                                sample++
                                if (isSampleOverload(sample, fastScan) || !isActive) return@withContext
                            }
                        }
                        extractor.endnoteText.forEachIndexed { index, text ->
                            getEntries(text, detectFunction).forEach {
                                locations.add(Location(it, "Endnote:$index"))
                            }
                            length += text.length
                            if (length >= FileType.Companion.scanSettings.sampleLength || !isActive) {
                                length = 0
                                sample++
                                if (isSampleOverload(sample, fastScan) || !isActive) return@withContext
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            try {
                withContext(Dispatchers.IO) {
                    val file = File(filePath)
                    val str = StringBuilder()
                    POIFSFileSystem(file).use { inputStream ->
                        HWPFOldDocument(inputStream).use { hwpfOldDocument ->
                            hwpfOldDocument.documentText.forEach { c ->
                                str.append(c)
                                if (str.length >= scanSettings.sampleLength || !isActive) {
                                    getEntries(str.toString(), detectFunction).forEach {
                                        locations.add(Location(it, ""))
                                    }
                                    str.clear()
                                    sample++
                                    if (FileType.Companion.isSampleOverload(sample, fastScan) || !isActive) return@withContext
                                }
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                throw ScanException
            }
        }
        return locations
    }
}
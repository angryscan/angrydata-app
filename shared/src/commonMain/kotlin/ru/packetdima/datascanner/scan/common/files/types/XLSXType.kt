package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.dhatim.fastexcel.reader.ReadableWorkbook
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.Location
import ru.packetdima.datascanner.scan.common.files.LocationFinder.ScanException
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext

object XLSXType : IFileType {
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
                    ReadableWorkbook(inputStream).use { workbook ->
                        workbook.sheets.use { sheets ->
                            sheets.forEach sheet@{ sheet ->
                                sheet?.openStream().use { rowStream ->
                                    rowStream?.forEach rowStream@{ row ->
                                        if (isSampleOverload(sample, fastScan, isActive))
                                            return@rowStream
                                        row?.forEach { cell ->
                                            if (cell != null) {
                                                str.append(cell.text).append("\n")
                                                if (isLengthOverload(str.length, isActive)) {
                                                    res + scan(str.toString(), detectFunctions)
                                                    str.clear()
                                                    sample++
                                                    if (isSampleOverload(sample, fastScan, isActive))
                                                        return@rowStream
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            res.skip()
            return res
        }
        if (str.isNotEmpty()) {
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
                    ReadableWorkbook(inputStream).use { workbook ->
                        workbook.sheets.use { sheets ->
                            sheets.forEach sheet@{ sheet ->
                                sheet?.openStream().use { rowStream ->
                                    rowStream?.forEach rowStream@{ row ->
                                        if (isSampleOverload(sample, fastScan, isActive)) return@rowStream

                                        row?.forEach { cell ->
                                            if (cell != null) {
                                                getEntries(cell.text, detectFunction)
                                                    .forEach {
                                                        locations.add(Location(it, "${sheet.name}:${cell.address}"))
                                                    }

                                                length += cell.text.length
                                                if (isLengthOverload(length, isActive)) {
                                                    length = 0
                                                    sample++
                                                    if (isSampleOverload(sample, fastScan, isActive))
                                                        return@rowStream
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            throw ScanException
        }
        return locations
    }
}
package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.FileType
import ru.packetdima.datascanner.scan.common.files.FileType.Companion.scanSettings
import ru.packetdima.datascanner.scan.common.files.Location
import ru.packetdima.datascanner.scan.common.files.LocationFinder.ScanException
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext

object XLSType : IFileType {
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
            //Create Workbook instance holding reference to .xlsx file
            withContext(Dispatchers.IO) {
                FileInputStream(file).use { fileInputStream ->
                    HSSFWorkbook(fileInputStream).use { workbook ->
                        val dataFormatter = DataFormatter()
                        dataFormatter.isEmulateCSV = true
                        workbook.forEach workbook@{ sheet ->
                            sheet?.forEach { row ->
                                row?.forEach { cell ->
                                    if (cell != null) {
                                        when (cell.cellType) {
                                            CellType.NUMERIC -> str.append(dataFormatter.formatCellValue(cell))
                                                .append("\n")

                                            CellType.STRING -> str.append(dataFormatter.formatCellValue(cell))
                                                .append("\n")

                                            else -> {}
                                        }
                                        if (str.length >= scanSettings.sampleLength || !isActive) {
                                            res + withContext(context) { scan(str.toString(), detectFunctions) }
                                            str.clear()
                                            sample++
                                            if (isSampleOverload(sample, fastScan) || !isActive) return@withContext
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
        if (str.isNotEmpty() && !isSampleOverload(sample, fastScan)) {
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
                FileInputStream(file).use { fileInputStream ->
                    HSSFWorkbook(fileInputStream).use { workbook ->
                        val dataFormatter = DataFormatter()
                        dataFormatter.isEmulateCSV = true
                        workbook.forEach workbook@{ sheet ->
                            sheet?.forEach { row ->
                                row?.forEach { cell ->
                                    if (cell != null) {
                                        val text = when (cell.cellType) {
                                            CellType.NUMERIC -> dataFormatter.formatCellValue(cell)

                                            CellType.STRING -> dataFormatter.formatCellValue(cell)

                                            else -> ""
                                        }

                                        getEntries(text, detectFunction)
                                            .forEach {
                                                locations.add(Location(it, "${sheet.sheetName}:${cell.address}"))
                                            }

                                        if (length >= FileType.Companion.scanSettings.sampleLength || !isActive) {
                                            length = 0
                                            sample++
                                            if (isSampleOverload(sample, fastScan) || !isActive) return@withContext
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
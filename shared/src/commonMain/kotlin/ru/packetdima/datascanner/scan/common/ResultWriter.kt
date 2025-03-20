package ru.packetdima.datascanner.scan.common

import info.downdetector.bigdatascanner.common.DetectFunction
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dhatim.fastexcel.BorderStyle
import org.dhatim.fastexcel.Workbook
import org.jetbrains.compose.resources.getString
import ru.packetdima.datascanner.common.AppVersion
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.TaskFileResult
import ru.packetdima.datascanner.ui.strings.readableName
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset

val logging = KotlinLogging.logger {}

object ResultWriter {
    enum class FileExtensions(val extension: String) {
        CSV("csv"),
        XLSX("xlsx"),
//        PDF("pdf"),
//        XML("xml"),
    }

    suspend fun saveResult(filePath: String, result: List<TaskFileResult>, onSaveError: () -> Unit): Boolean {
        val extension = FileExtensions.entries.find { filePath.endsWith(".${it.extension}") }
        if (extension == null) {
            onSaveError()
            return false
        }


        if (File(filePath).exists() && !File(filePath).delete()) {
            onSaveError()
            return false
        }


        try {
            when (extension) {
                FileExtensions.CSV -> writeCSV(File(filePath), result = result)
                FileExtensions.XLSX -> writeXLSX(File(filePath), result = result)
            }
            return true
        } catch (e: Exception) {
            logging.error { "Failed to save report. ${e.message}" }
            onSaveError()
            return false
        }
    }

    private suspend fun writeCSV(reportFile: File, reportEncoding: String = "UTF-8", result: List<TaskFileResult>) {
        withContext(Dispatchers.IO) {
            FileOutputStream(reportFile, true).bufferedWriter(charset = Charset.forName(reportEncoding))
        }.use { writer ->
            val columns = listOf(
                getString(Res.string.Result_ColumnFile),
                getString(Res.string.Result_ColumnAttributes),
                getString(Res.string.Result_ColumnScore),
                getString(Res.string.Result_ColumnCount),
                getString(Res.string.Result_ColumnSize)
            )
            writer.append(
                columns.joinToString(";") + "\r\n"
            )

            result.forEach { fileRow ->
                writer.append(
                    listOf(
                        fileRow.path,
                        fileRow.foundAttributes.map { attr -> if (attr is DetectFunction) attr.readableName() else attr.writeName }
                            .joinToString(", "),
                        fileRow.score.toString(),
                        fileRow.count.toString(),
                        fileRow.size.toString()
                    ).joinToString(";") + "\r\n"
                )
            }
        }
    }

    private suspend fun writeXLSX(reportFile: File, result: List<TaskFileResult>) {
        val columns = listOf(
            getString(Res.string.Result_ColumnFile),
            getString(Res.string.Result_ColumnAttributes),
            getString(Res.string.Result_ColumnScore),
            getString(Res.string.Result_ColumnCount),
            getString(Res.string.Result_ColumnSize)
        )
        withContext(Dispatchers.IO) {
            FileOutputStream(reportFile)
        }.use { outputStream ->
            Workbook(
                outputStream,
                "Big Data Scanner",
                if (AppVersion == "Debug") "0.1" else AppVersion.substringBeforeLast('.')
            ).use { workbook ->
                val sheet = workbook.newWorksheet(getString(Res.string.Result_SheetName))
                columns.forEachIndexed { index, column ->
                    sheet.value(0, index, column)
                }

                result.forEachIndexed { index, fileRow ->
                    sheet.value(index + 1, 0, fileRow.path)
                    sheet.value(
                        index + 1,
                        1,
                        fileRow.foundAttributes.map { attr -> if (attr is DetectFunction) attr.readableName() else attr.writeName }
                            .joinToString(", "))
                    sheet.value(index + 1, 2, fileRow.score.toString())
                    sheet.value(index + 1, 3, fileRow.count.toString())
                    sheet.value(index + 1, 4, fileRow.size.toString())
                }


                sheet
                    .range(0, 0, result.size, columns.size - 1)
                    .style()
                    .borderStyle(BorderStyle.THIN)
                    .set()
                sheet
                    .range(0, 0, 0, columns.size)
                    .style()
                    .bold()
                    .set()

                sheet.freezePane(columns.size, 1)
                sheet.setAutoFilter(0, 0, columns.size)
            }
        }
    }
}
package ru.packetdima.datascanner.scan.common

import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import ru.packetdima.datascanner.common.OS
import ru.packetdima.datascanner.resources.*
import ru.packetdima.datascanner.scan.TaskFileResult
import ru.packetdima.datascanner.ui.strings.readableName
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import javax.swing.JOptionPane

object ResultWriter {
    enum class FileExtensions(val extension: String) {
        CSV("csv"),
        XLSX("xlsx"),
        PDF("pdf"),
        XML("xml"),
    }

    suspend fun saveResult(filePath: String, result: List<TaskFileResult>, onSaveError: () -> Unit): Boolean {
        val extension = FileExtensions.entries.find { filePath.endsWith(".${it.extension}") }
        if (extension == null)
            return false


        if (File(filePath).exists() && !File(filePath).delete()) {
            onSaveError()
//            JOptionPane.showMessageDialog(
//                null,
//                getString(Res.string.FileSave_ErrorText),
//                getString(Res.string.FileSave_ErrorTitle),
//                JOptionPane.ERROR_MESSAGE
//            )
            return false
        }


        try {
            write(
                reportFile = filePath,
                reportEncoding = when (OS.currentOS()) {
                    OS.WINDOWS -> "Windows-1251"
                    else -> "UTF-8"
                },
                result = result
            )
            return true
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(null, e.message, "Error", JOptionPane.ERROR_MESSAGE)
            return false
        }
    }

    suspend fun write(reportFile: String, reportEncoding: String = "UTF-8", result: List<TaskFileResult>) =
        write(File(reportFile), reportEncoding, result)

    suspend fun write(reportFile: File, reportEncoding: String = "UTF-8", result: List<TaskFileResult>) {
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
}
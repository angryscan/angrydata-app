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
import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

object ResultWriter {

    suspend fun saveResult(fileName: String, result: List<TaskFileResult>): Boolean {
        val f = JFileChooser()
        f.fileSelectionMode = JFileChooser.FILES_ONLY
        f.isMultiSelectionEnabled = false
        f.fileFilter = FileNameExtensionFilter("CSV (*.csv)", "csv")
        f.selectedFile = File(fileName)
        val resDialog = f.showSaveDialog(null)
        if (resDialog != JFileChooser.APPROVE_OPTION)
            return false
        var path = f.selectedFile.absolutePath

        if (!path.endsWith(".csv"))
            path += ".csv"


        if (File(path).exists()) {
            JDialog.setDefaultLookAndFeelDecorated(true)
            val response = JOptionPane.showConfirmDialog(
                null,
                getString(Res.string.FileSave_AlreadyExistText),
                getString(Res.string.FileSave_AlreadyExistTitle),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            )
            if (response == JOptionPane.YES_OPTION) {
                if (!File(path).delete()) {
                    JOptionPane.showMessageDialog(
                        null,
                        getString(Res.string.FileSave_ErrorText),
                        getString(Res.string.FileSave_ErrorTitle),
                        JOptionPane.ERROR_MESSAGE
                    )
                    return false
                }
            } else
                return false
        }


        try {
            write(
                reportFile = path,
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
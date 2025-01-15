package ru.packetdima.datascanner.searcher

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.times
import org.jetbrains.exposed.sql.transactions.transaction
import ru.packetdima.datascanner.misc.FileSize
import ru.packetdima.datascanner.misc.OS
import ru.packetdima.datascanner.searcher.model.*
import ru.packetdima.datascanner.searcher.model.ResultRow
import java.io.File
import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

class SearcherResult {
    companion object {
        fun initResultTable() {
            transaction {

                SchemaUtils.drop(ResultRows)
                SchemaUtils.create(ResultRows)
            }
        }

        fun createResult(): Pair<Long, FileSize> {
            var count = 0L
            val filesSize = FileSize()

            initResultTable()

            transaction {
                val reportFileID = Reports.file.alias("reportFileID")
                val reportValueCountCol = Reports.count.sum().alias("ReportValueCount")
                val reportAttributeCountCol = Reports.attribute.count().alias("ReportAttributeCount")
                val reportScoreCol = (Reports.count * Attributes.factor).sum().alias("ReportScore") //Сумма скоров
                val attributeNamesCol = CustomStringFunction(
                    "GROUP_CONCAT",
                    Attributes.translate,
                    stringParam(", ")
                ).alias("AttributeNames")
                val attributeNamesIntCol = CustomStringFunction(
                    "GROUP_CONCAT",
                    Attributes.name,
                    stringParam(", ")
                ).alias("AttributeInternalNames")

                val reportQuery = (Reports innerJoin Attributes)
                    .select(
                        reportFileID,
                        reportValueCountCol,
                        attributeNamesCol,
                        reportScoreCol,
                        reportAttributeCountCol,
                        attributeNamesIntCol
                    )
                    .groupBy(reportFileID)
                    .alias("ReportScore")

                reportQuery
                    .innerJoin(ScannedFiles, { reportQuery[reportFileID] }, { ScannedFiles.id })
                    .select(
                        ScannedFiles.path, //Путь к файлу
                        ScannedFiles.size, //Размер файла
                        reportQuery[reportValueCountCol], //Сумма количества аттрибутов
                        reportQuery[reportScoreCol], //Сумма скоров
                        reportQuery[attributeNamesCol], //Имена всех атрибутов
                        reportQuery[reportAttributeCountCol], //Количество типов атрибутов
                        reportQuery[attributeNamesIntCol]
                    )
                    .forEach {
                        val scoreSum = if (it[reportQuery[attributeNamesIntCol]]!!.contains("full_names")) {
                            it[reportQuery[reportScoreCol]]!!.toLong() * (it[reportQuery[reportAttributeCountCol]] + 20)
                        } else {
                            it[reportQuery[reportScoreCol]]!!.toLong() * it[reportQuery[reportAttributeCountCol]]
                        }

                        ResultRow.new {
                            path = it[ScannedFiles.path]
                            score = scoreSum
                            attrCount = it[reportQuery[reportValueCountCol]]!!
                            attrNames = it[reportQuery[attributeNamesCol]]!!
                            fileSize = it[ScannedFiles.size]
                        }
                        count++
                        filesSize.plus(it[ScannedFiles.size])
                    }
            }
            return Pair(count, filesSize)
        }

        val columnNames = listOf(
            "File name",
            "Score",
            "Attributes count",
            "Attributes found",
            "File size"
        )

        fun getPreview(rowCount: Int): List<List<String>> {
            val res: MutableList<List<String>> = mutableListOf()

            transaction {

                val query = if (rowCount == 0)
                    ResultRows
                        .selectAll()
                        .orderBy(ResultRows.score, SortOrder.DESC)
                else
                    ResultRows
                        .selectAll()
                        .limit(rowCount)
                        .orderBy(ResultRows.score, SortOrder.DESC)
                query.forEach { row ->
                    res.add(
                        listOf(
                            row[ResultRows.path].toString(),
                            row[ResultRows.score].toString(),
                            row[ResultRows.attrCount].toString(),
                            row[ResultRows.attrNames].toString(),
                            (row[ResultRows.fileSize].toLong() / 1024).toString() + "KB"
                        )
                    )
                }
            }

            return res
        }

        fun saveResult(fileName: String): Boolean {
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
                    "File already exists! Do you want to replace it?",
                    "File Already Exist!",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                )
                if (response == JOptionPane.YES_OPTION) {
                    if (!File(path).delete()) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Cannot save file. Check it is in use by another process!",
                            "Error!",
                            JOptionPane.ERROR_MESSAGE
                        )
                        return false
                    }
                } else
                    return false
            }


            try {
                Writer.writeReport(
                    reportFile = path,
                    reportEncoding = when (OS.currentOS()) {
                        OS.WINDOWS -> "Windows-1251"
                        else -> "UTF-8"
                    }
                )
                return true
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(null, e.message, "Error", JOptionPane.ERROR_MESSAGE)
                return false
            }
        }

        fun deleteFile(filePath: String): Boolean {
            return if (File(filePath).delete()) {
                transaction {
                    ResultRows.deleteWhere { path.eq(filePath) }
                }
                true
            } else
                false
        }

        fun deleteAllFiles(): Int {
            var deletedFiles = 0
            transaction {
                ResultRows
                    .select(ResultRows.path)
                    .forEach { row ->
                        if (File(row[ResultRows.path]).delete()) {
                            deletedFiles++
                        }
                    }
            }
            return deletedFiles
        }
    }
}
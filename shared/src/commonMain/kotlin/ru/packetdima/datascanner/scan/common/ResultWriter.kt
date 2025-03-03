package ru.packetdima.datascanner.scan.common

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.packetdima.datascanner.scan.TaskFileResult
import ru.packetdima.datascanner.searcher.model.ResultRows
import ru.packetdima.datascanner.searcher.model.readableName
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset

object ResultWriter {
    fun write(reportFile: String, reportEncoding: String = "UTF-8", result: List<TaskFileResult>) =
        write(File(reportFile), reportEncoding, result)

    fun write(reportFile: File, reportEncoding: String = "UTF-8", result: List<TaskFileResult>) {
        FileOutputStream(reportFile, true).bufferedWriter(charset = Charset.forName(reportEncoding)).use { writer ->
            transaction {
                writer.append(
                    runBlocking {
                        ResultRows.columns.filter { it.name != "id" }.map { it.readableName() }
                    }.joinToString(";") + "\r\n"
                )

                val query = ResultRows
                    .selectAll()
                    .orderBy(ResultRows.score, SortOrder.DESC)
                query.forEach { row ->
                    writer.append(
                        listOf(
                            row[ResultRows.path].toString(),
                            row[ResultRows.score].toString(),
                            row[ResultRows.attrCount].toString(),
                            row[ResultRows.attrNames].toString(),
                            (row[ResultRows.fileSize].toLong() / 1024).toString() + "KB"
                        ).joinToString(";") + "\r\n"
                    )
                }
            }
        }
    }
}
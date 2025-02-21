package ru.packetdima.datascanner.searcher

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.searcher.model.*
import ru.packetdima.datascanner.ui.strings.readableName
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset

object Writer: KoinComponent {
    suspend fun write(report: List<Document>) {
        if (report.isEmpty())
            return

        writeSemaphore.withPermit {
            transaction {

                for (document in report) {

                    val scannedFile = ScannedFile.new {
                        path = document.path
                        size = document.size
                    }

                    document.getDocumentFields().forEach { (key, value) ->
                        val attr = Attribute.find { Attributes.name eq key.writeName }.firstOrNull()
                        if (attr != null) {
                            Report.new {
                                count = value
                                attribute = attr
                                file = scannedFile
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun initDB() {
        val scanSettings: ScanSettings by inject()
        writeSemaphore.withPermit {
            transaction {
                SchemaUtils.drop(
                    Attributes,
                    ScannedFiles,
                    Reports
                )
                SchemaUtils.create(
                    Attributes,
                    ScannedFiles,
                    Reports
                )

                for (attr in scanSettings.detectFunctions) {
                    Attribute.new {
                        name = attr.writeName
                        factor = when (attr.writeName) {
                            "full_names" -> 5
                            "card_numbers" -> 30
                            "account_number" -> 30
                            else -> 1
                        }
                        translate = runBlocking { attr.readableName() }
                    }
                }
                for (attr in scanSettings.userSignature) {
                    Attribute.new {
                        name = attr.writeName
                        factor = 1
                        translate = runBlocking { attr.writeName }
                    }
                }
            }
        }
    }

    fun writeReport(reportFile: String, reportEncoding: String = "UTF-8") =
        writeReport(File(reportFile), reportEncoding)

    fun writeReport(reportFile: File, reportEncoding: String = "UTF-8") {
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

    private val writeSemaphore = Semaphore(1)
}
package ru.packetdima.datascanner.searcher

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.searcher.model.*
import ru.packetdima.datascanner.ui.strings.readableName

object Writer {
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

                for (attr in Settings.searcher.detectFunctions) {
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
            }
        }
    }

    private val writeSemaphore = Semaphore(1)
}
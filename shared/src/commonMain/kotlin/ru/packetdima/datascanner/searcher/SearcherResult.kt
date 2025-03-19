package ru.packetdima.datascanner.searcher

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.times
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.scan.common.FileSize
import ru.packetdima.datascanner.searcher.model.*
import ru.packetdima.datascanner.searcher.model.ResultRow

class SearcherResult {
    companion object: KoinComponent {
        private val database by inject<DatabaseConnector>()
        fun initResultTable() {
            runBlocking {
                database.transaction {

                    SchemaUtils.drop(ResultRows)
                    SchemaUtils.create(ResultRows)
                }
            }
        }

        fun createResult(): Pair<Long, FileSize> {
            var count = 0L
            val filesSize = FileSize()

            initResultTable()

            runBlocking {
                database.transaction {
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

            runBlocking {
                database.transaction {

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
            }

            return res
        }
    }
}
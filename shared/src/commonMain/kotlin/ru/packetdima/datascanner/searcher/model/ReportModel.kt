package ru.packetdima.datascanner.searcher.model

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import ru.packetdima.datascanner.ui.datatable.TableHeader
import ru.packetdima.datascanner.resources.*

object ScannedFiles : LongIdTable() {
    val path = text("path")
    val size = long("size")
}

class ScannedFile(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ScannedFile>(
        ScannedFiles
    )

    var path by ScannedFiles.path
    var size by ScannedFiles.size
}

object Attributes : IntIdTable() {
    val name = varchar("name", 50)
    val translate = varchar("translate", 50)
    val factor = integer("factor")
}

class Attribute(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Attribute>(
        Attributes
    )

    var name by Attributes.name
    var translate by Attributes.translate
    var factor by Attributes.factor
}

object Reports : LongIdTable() {
    val file = reference("file", ScannedFiles)
    val attribute = reference("attribute", Attributes)
    val count = integer("count")
}

class Report(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Report>(
        Reports
    )

    var file by ScannedFile referencedOn Reports.file
    var attribute by Attribute referencedOn Reports.attribute
    var count by Reports.count
}

object ResultRows : LongIdTable() {
    @TableHeader("File name", 0, 1f)
    val path = text("path")
    @TableHeader("Score", 1, 0.4f)
    val score = long("score")
    @TableHeader("Attributes count", 2, 0.3f)
    val attrCount = integer("attrCount")
    @TableHeader("Attributes found", 3, 0.4f)
    val attrNames = varchar("attrNames", 300)
    @TableHeader("File size", 4, 0.3f)
    val fileSize = long("fileSize")
}

@OptIn(ExperimentalResourceApi::class)
suspend fun Column<*>.readableName(): String{
    return when(this.name) {
        "path" -> getString(Res.string.colPath)
        "score" -> getString(Res.string.colScore)
        "attrCount" -> getString(Res.string.colCount)
        "attrNames" -> getString(Res.string.colAttributes)
        "fileSize" -> getString(Res.string.colFileSize)
        else -> this.name
    }
}

class ResultRow(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ResultRow>(
        ResultRows
    )

    var path by ResultRows.path
    var score by ResultRows.score
    var attrCount by ResultRows.attrCount
    var attrNames by ResultRows.attrNames
    var fileSize by ResultRows.fileSize
}
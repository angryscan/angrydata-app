package ru.packetdima.datascanner.db.models

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

val format = Json { prettyPrint = false }

object TaskFileScanResults: IntIdTable() {
    val file = reference("file", TaskFiles)
    val detectFunction = reference("detect_function", TaskDetectFunctions)
    val count = integer("count")
}

class TaskFileScanResult(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<TaskFileScanResult>(TaskFileScanResults)
    var file by TaskFile referencedOn TaskFileScanResults.file
    var detectFunction by TaskDetectFunction referencedOn TaskFileScanResults.detectFunction
    var count by TaskFileScanResults.count
}
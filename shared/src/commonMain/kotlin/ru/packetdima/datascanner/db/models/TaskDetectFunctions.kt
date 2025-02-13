package ru.packetdima.datascanner.db.models

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.json.json

val formatter = Json { prettyPrint = false }

object TaskDetectFunctions: IntIdTable() {
    val task = reference("task", Tasks)
    val function = json<IDetectFunction>("function", formatter)
}

class TaskDetectFunction(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<TaskDetectFunction>(TaskDetectFunctions)

    var task by Task referencedOn TaskDetectFunctions.task
    var function by TaskDetectFunctions.function
}
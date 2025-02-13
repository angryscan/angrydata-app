package ru.packetdima.datascanner.db.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Tasks : IntIdTable() {
    val path = text("path")
    val taskState = enumeration("task_state", TaskState::class)
    val startedAt = datetime("started_at").nullable()
    val finishedAt = datetime("finished_at").nullable()
    val size = text("size").nullable()
    val filesCount = long("files_count").nullable()
    val fastScan = bool("fast_scan").default(false)
}

class Task(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Task>(Tasks)

    var path by Tasks.path
    var taskState by Tasks.taskState
    var startedAt by Tasks.startedAt
    var finishedAt by Tasks.finishedAt
    var size by Tasks.size
    var filesCount by Tasks.filesCount
    var fastScan by Tasks.fastScan

    val files by TaskFile referrersOn TaskFiles.task
    val extensions by TaskFileExtension referrersOn TaskFileExtensions.task
    val detectFunctions by TaskDetectFunction referrersOn TaskDetectFunctions.task
}
package ru.packetdima.datascanner.scan

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.db.models.*
import ru.packetdima.datascanner.scan.common.FileType

class ScanService : KoinComponent {
    private val database: DatabaseConnector by inject()

    private val appSettings: AppSettings by inject()
    private val scanSettings: ScanSettings by inject()

    val tasks: TasksViewModel by inject()

    private var scanThreads: Array<ScanThread>

    init {
        transaction(database.connection) {
            SchemaUtils.create(
                Tasks,
                TaskFiles,
                TaskFileExtensions,
                TaskDetectFunctions,
                TaskFileScanResults
            )
        }
        scanThreads = Array(appSettings.threadCount.value) { ScanThread() }
        CoroutineScope(Dispatchers.IO).launch {
            database.transaction {
                Task.all().forEach { task ->

                    val taskEntity = TaskEntityViewModel(
                        dbTask = task,
                        state = task.taskState,
                        totalFiles = task.filesCount,
                        foundAttributes = (TaskFileScanResults innerJoin TaskFiles innerJoin TaskDetectFunctions)
                            .select(TaskDetectFunctions.function)
                            .where { TaskFiles.task.eq(task.id) }
                            .withDistinct()
                            .map { it[TaskDetectFunctions.function] }
                            .toSet(),
                        foundFiles = TaskFiles
                            .select(TaskFiles.id)
                            .where { TaskFiles.task.eq(task.id) }
                            .withDistinct()
                            .count()
                    )
                    if (task.taskState == TaskState.SCANNING)
                        taskEntity.setState(TaskState.STOPPED)

                    tasks.add(taskEntity)
                }
            }
        }
    }

    fun start() = scanThreads.forEach {
        if (!it.started)
            it.start()
    }

    suspend fun stop() = scanThreads.map {
        if (it.started)
            it.stop()
    }

    fun setThreadsCount() {
        val scanStarted = scanThreads.any { it.started }
        if (scanStarted)
            runBlocking { stop() }

        scanThreads = Array(appSettings.threadCount.value) { ScanThread() }

        if (scanStarted)
            start()
    }

    suspend fun createTask(
        path: String,
        extensions: List<FileType>? = null,
        detectFunctions: List<IDetectFunction>? = null,
        fastScan: Boolean? = null
    ) {
        return database.transaction {
            val task = Task.new {
                this.path = path
                this.taskState = TaskState.PENDING
                this.fastScan = fastScan ?: scanSettings.fastScan.value
            }
            if (extensions != null) {
                extensions.forEach { ext ->
                    TaskFileExtension.new {
                        this.task = task
                        this.extension = ext
                    }
                }
            } else {
                scanSettings.extensions.forEach { ext ->
                    TaskFileExtension.new {
                        this.task = task
                        this.extension = ext
                    }
                }
            }

            if (detectFunctions != null) {
                detectFunctions.forEach { df ->
                    TaskDetectFunction.new {
                        this.task = task
                        this.function = df
                    }
                }
            } else {
                scanSettings.detectFunctions.forEach { df ->
                    TaskDetectFunction.new {
                        this.task = task
                        this.function = df
                    }
                }
            }

            tasks.add(TaskEntityViewModel(task))
        }
    }

    suspend fun deleteTask(task: TaskEntityViewModel) {
        database.transaction {
            TaskFileExtensions.deleteWhere {
                TaskFileExtensions.task.eq(task.dbTask.id)
            }
            TaskDetectFunctions.deleteWhere {
                TaskDetectFunctions.task.eq(task.dbTask.id)
            }
            TaskFiles.deleteWhere {
                TaskFiles.task.eq(task.dbTask.id)
            }
            task.dbTask.delete()
        }
        tasks.delete(task)
    }

    fun startTask(task: TaskEntityViewModel) {
        task.start()
        this.start()
    }

    fun stopTask(task: TaskEntityViewModel) = task.stop()

    fun resumeTask(task: TaskEntityViewModel) = task.resume(false)

    @Suppress("Unused")
    fun rescanTask(task: TaskEntityViewModel) = task.resume(true)
}
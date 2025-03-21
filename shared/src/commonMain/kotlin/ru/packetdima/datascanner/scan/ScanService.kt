package ru.packetdima.datascanner.scan

import info.downdetector.bigdatascanner.common.IDetectFunction
import io.github.oshai.kotlinlogging.KotlinLogging
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
import ru.packetdima.datascanner.common.LogMarkers
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.db.models.*
import ru.packetdima.datascanner.scan.common.FileType

private val logging = KotlinLogging.logger {}

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
                    if (task.taskState == TaskState.SCANNING) {
                        taskEntity.setState(TaskState.STOPPED)
                        logging.info(throwable = null, LogMarkers.UserAction) {
                            "Stopped task after restart (${taskEntity.id.value}) ${taskEntity.path.value}"
                        }
                    }

                    tasks.add(taskEntity)
                }
            }
        }
    }

    fun start() {
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Starting scan threads"
        }
        scanThreads.forEach {

            if (!it.started)
                it.start()
        }
    }

    suspend fun stop() {
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Stopping scan threads"
        }
        scanThreads.map {
            if (it.started)
                it.stop()
        }
    }

    fun setThreadsCount() {
        val scanStarted = scanThreads.any { it.started }
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Setting scan threads to ${appSettings.threadCount.value}"
        }
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
    ): TaskEntityViewModel {

        return database.transaction {
            val task = Task.new {
                this.path = path
                this.taskState = TaskState.PENDING
                this.fastScan = fastScan ?: scanSettings.fastScan.value
            }
            logging.info(throwable = null, LogMarkers.UserAction) {
                "Creating task. " +
                        "ID: ${task.id.value}. " +
                        "Path: \"$path\". " +
                        "Extensions: ${
                            (if (extensions != null)
                                extensions else
                                scanSettings.extensions).joinToString { it.name }
                        }. " +
                        "Detect functions: ${
                            (if (detectFunctions != null)
                                detectFunctions
                            else (scanSettings.detectFunctions + scanSettings.userSignatures)
                                    ).joinToString { it.name }
                        }. " +
                        "Fast scan: ${fastScan ?: scanSettings.fastScan.value}. " +
                        "Threads: ${appSettings.threadCount.value}"
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
                scanSettings.userSignatures.forEach { df ->
                    TaskDetectFunction.new {
                        this.task = task
                        this.function = df
                    }

                }
            }

            val taskEntity = TaskEntityViewModel(task)
            tasks.add(taskEntity)
            taskEntity
        }
    }

    suspend fun deleteTask(task: TaskEntityViewModel) {
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Delete task. ID: ${task.id.value}. Path: \"${task.path.value}\""
        }
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
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Starting task. ID: ${task.id.value}. Path: \"${task.path.value}\""
        }
        task.start()
        this.start()
    }

    fun stopTask(task: TaskEntityViewModel) {
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Stopping task. ID: ${task.id.value}. Path: \"${task.path.value}\""
        }
        task.stop()
    }

    fun resumeTask(task: TaskEntityViewModel) {
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Resume task. ID: ${task.id.value}. Path: \"${task.path.value}\""
        }
        task.resume(false)
        this.start()
    }

    @Suppress("Unused")
    fun rescanTask(task: TaskEntityViewModel) {
        logging.info(throwable = null, LogMarkers.UserAction) {
            "Restart task. ID: ${task.id.value}. Path: \"${task.path.value}\""
        }
        task.resume(true)
        this.start()
    }
}
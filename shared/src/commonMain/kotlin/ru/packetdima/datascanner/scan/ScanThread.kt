package ru.packetdima.datascanner.scan

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.db.models.TaskDetectFunctions
import ru.packetdima.datascanner.db.models.TaskFileScanResults
import ru.packetdima.datascanner.db.models.TaskFiles
import ru.packetdima.datascanner.db.models.TaskState
import ru.packetdima.datascanner.scan.common.FileType
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class ScanThread : KoinComponent {
    private val scanThreadScope = CoroutineScope(Dispatchers.Default)

    private val database: DatabaseConnector by inject()

    private val tasks: TasksViewModel by inject()

    private val scanningFileId: AtomicInteger = AtomicInteger(-1)

    private val _started = AtomicBoolean(false)
    val started: Boolean get() = _started.get()
    private val stopRequested = AtomicBoolean(false)

    private var retryCount = 0

    suspend fun stop() {
        stopRequested.set(true)
        scanThreadScope.launch {
            while (_started.get())
                delay(1000)
        }.join()
    }

    fun start() {
        _started.set(true)
        scanThreadScope.launch {
            while (_started.get() && !stopRequested.get()) {
                yield()
                val tasksToScan = tasks.tasks.value.filter { it.state.value == TaskState.SCANNING }
                if (tasksToScan.count() == 0) {
                    retryCount++
                    if (retryCount > 3) {
                        _started.set(false)
                        retryCount = 0
                    }
                    delay(1000)
                    continue
                }

                val taskEntity = tasks.tasks.value.filter { it.state.value == TaskState.SCANNING }.random()

                val fastScan = database.transaction { taskEntity.dbTask.fastScan }

                val dbFile = database.transaction {
                    val resultRow = TaskFiles.selectAll()
                        .where {
                            TaskFiles.task.eq(taskEntity.dbTask.id) and
                                    TaskFiles.state.eq(TaskState.SEARCHING)
                        }
                        .limit(1)
                        .firstOrNull()
                    if (resultRow != null) {
                        TaskFiles.update(
                            where = {
                                TaskFiles.id.eq(resultRow[TaskFiles.id])
                            }
                        ) {
                            it[state] = TaskState.SCANNING
                        }
                    }

                    resultRow
                }

                if (dbFile == null) {
                    scanningFileId.set(-1)

                    retryCount++
                    if (retryCount > 3) {
                        _started.set(false)
                        retryCount = 0
                    }
                    delay(1000)
                    continue
                }

                val fileId = dbFile[TaskFiles.id].value
                val filePath = dbFile[TaskFiles.path]
                val fileObject = File(filePath)
                val detectFunctions = database.transaction {
                    TaskDetectFunctions
                        .select(TaskDetectFunctions.function, TaskDetectFunctions.id)
                        .where { TaskDetectFunctions.task.eq(taskEntity.dbTask.id) }
                        .associate { it[TaskDetectFunctions.function] to it[TaskDetectFunctions.id].value }
                }

                scanningFileId.set(fileId)

                val scanRes = FileType
                    .getFileType(fileObject)
                    ?.scanFile(
                        file = fileObject,
                        context = currentCoroutineContext(),
                        detectFunctions = detectFunctions.map { it.key },
                        fastScan = fastScan
                    )

                if (scanRes != null && !scanRes.skipped()) {
                    database.transaction {
                        scanRes.getDocumentFields().forEach { field ->
                            TaskFileScanResults.insert {
                                it[file] = fileId
                                it[detectFunction] = detectFunctions[field.key] ?: 0
                                it[count] = field.value
                            }
                            taskEntity.addFoundAttribute(field.key)
                        }
                        if (!scanRes.isEmpty()) {
                            taskEntity.incrementFoundFiles()
                        }
                        TaskFiles.update(
                            where = {
                                TaskFiles.id.eq(fileId)
                            }
                        ) {
                            it[state] = TaskState.FAILED
                        }
                    }
                } else {
                    database.transaction {
                        TaskFiles.update(
                            where = {
                                TaskFiles.id.eq(fileId)
                            }
                        ) {
                            it[state] = TaskState.FAILED
                        }
                    }
                }

                taskEntity.checkProgress()
            }

            _started.set(false)
            stopRequested.set(false)
        }
    }
}
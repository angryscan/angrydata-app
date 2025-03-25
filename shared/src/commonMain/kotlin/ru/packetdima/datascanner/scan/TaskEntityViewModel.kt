package ru.packetdima.datascanner.scan

import androidx.lifecycle.ViewModel
import info.downdetector.bigdatascanner.common.IDetectFunction
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.common.LogMarkers
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.db.models.*
import ru.packetdima.datascanner.scan.common.FilesCounter
import ru.packetdima.datascanner.scan.functions.UserSignature
import java.io.File

private val logger = KotlinLogging.logger {}

class TaskEntityViewModel(
    val dbTask: Task,
    totalFiles: Long? = null,
    foundAttributes: Set<IDetectFunction>? = null,
    foundFiles: Long? = null,
    folderSize: String? = null,
    state: TaskState = TaskState.LOADING
) : ViewModel(), KoinComponent {
    private val database: DatabaseConnector by inject()

    private val taskScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private var _state = MutableStateFlow(state)
    val state
        get() = _state.asStateFlow()

    private var _busy = MutableStateFlow(false)
    val busy
        get() = _busy.asStateFlow()

    private var _id = MutableStateFlow<Int?>(null)
    val id = _id.asStateFlow()

    private var _totalFiles = MutableStateFlow(0L)
    val totalFiles
        get() = _totalFiles.asStateFlow()

    private var _selectedFiles = MutableStateFlow(0L)
    val selectedFiles
        get() = _selectedFiles.asStateFlow()

    private var _scannedFiles = MutableStateFlow(0L)
    val scannedFiles
        get() = _scannedFiles.asStateFlow()

    private var _foundFiles = MutableStateFlow(0L)
    val foundFiles
        get() = _foundFiles.asStateFlow()

    private var _skippedFiles = MutableStateFlow(0L)
    val skippedFiles
        get() = _skippedFiles.asStateFlow()

    private var _foundAttributes = MutableStateFlow(setOf<IDetectFunction>())
    val foundAttributes
        get() = _foundAttributes.asStateFlow()

    private var _fastScan = MutableStateFlow(false)
    val fastScan
        get() = _fastScan.asStateFlow()

    private var _path = MutableStateFlow("")
    val path
        get() = _path.asStateFlow()

    private var _startedAt = MutableStateFlow<LocalDateTime?>(null)
    val startedAt
        get() = _startedAt.asStateFlow()

    private var _finishedAt = MutableStateFlow<LocalDateTime?>(null)
    val finishedAt
        get() = _finishedAt.asStateFlow()

    private var _folderSize = MutableStateFlow("")
    val folderSize
        get() = _folderSize.asStateFlow()

    init {
        if (_state.value == TaskState.LOADING) {
            taskScope.launch {
                database.transaction {
                    _state.value = dbTask.taskState
                    _totalFiles.value = dbTask.filesCount ?: 0L
                    _folderSize.value = dbTask.size ?: ""
                }
            }
        } else {
            if (totalFiles != null)
                _totalFiles.value = totalFiles

            if (foundAttributes != null)
                _foundAttributes.value = foundAttributes

            if (foundFiles != null)
                _foundFiles.value = foundFiles

            if(folderSize != null)
                _folderSize.value = folderSize
        }

        taskScope.launch {
            database.transaction {
                _fastScan.value = dbTask.fastScan
                _path.value = dbTask.path
                _startedAt.value = dbTask.startedAt
                _finishedAt.value = dbTask.finishedAt
                _totalFiles.value = dbTask.filesCount ?: 0L

                _foundAttributes.value =
                    TaskFileScanResults
                        .innerJoin(TaskDetectFunctions)
                        .select(TaskDetectFunctions.function)
                        .where { TaskDetectFunctions.task.eq(dbTask.id) }
                        .map { it[TaskDetectFunctions.function] }
                        .toSet()
            }
        }
        _id.value = dbTask.id.value
        checkProgress()
    }

    fun addFoundAttribute(detectFunction: IDetectFunction) {
        //Check if user signature already added
        if (
            detectFunction !is UserSignature ||
            _foundAttributes.value.filter { it.name == detectFunction.name }.isEmpty()
        )
            _foundAttributes.value += detectFunction
    }

    fun incrementFoundFiles() {
        _foundFiles.value++
    }

    fun checkProgress() {
        taskScope.launch {
            while (_state.value == TaskState.LOADING) {
                delay(500)
            }
            if (_state.value != TaskState.PENDING && _state.value != TaskState.SEARCHING) {
                database.transaction {
                    if (_selectedFiles.value == 0L) {
                        _selectedFiles.value = TaskFiles
                            .selectAll()
                            .where {
                                TaskFiles.task.eq(dbTask.id)
                            }
                            .count()
                    }


                    _skippedFiles.value = TaskFiles
                        .selectAll()
                        .where {
                            TaskFiles.task.eq(dbTask.id) and TaskFiles.state.eq(TaskState.FAILED)
                        }
                        .count()

                    _scannedFiles.value = TaskFiles
                        .selectAll()
                        .where {
                            TaskFiles.task.eq(dbTask.id) and TaskFiles.state.eq(TaskState.COMPLETED)
                        }
                        .count()

                    if (_selectedFiles.value == _scannedFiles.value + _skippedFiles.value) {
                        if (_state.value != TaskState.COMPLETED) {
                            setState(TaskState.COMPLETED)
                            logger.info(
                                throwable = null,
                                LogMarkers.UserAction
                            ) { "Scanning task completed. ID: ${_id.value}. Path: \"${_path.value}\"" }
                        }
                    }
                }

                _busy.value = false
            }
        }
    }

    fun setState(state: TaskState) {
        logger.debug { "Task state changed to $state. ID: ${_id.value}. Path: \"${_path.value}\"" }

        taskScope.launch {
            while (_busy.value)
                delay(1000)

            _state.value = state

            _busy.value = true
            database.transaction {
                dbTask.taskState = state

                when (state) {
                    TaskState.SEARCHING -> {
                        dbTask.startedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        _startedAt.value = dbTask.startedAt

                        dbTask.finishedAt = null
                        _finishedAt.value = dbTask.finishedAt
                    }

                    TaskState.COMPLETED -> {
                        dbTask.finishedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        _finishedAt.value = dbTask.finishedAt
                    }

                    TaskState.STOPPED -> {
                        TaskFiles.update(
                            where = {
                                TaskFiles.task.eq(dbTask.id) and
                                        TaskFiles.state.neq(TaskState.COMPLETED) and
                                        TaskFiles.state.neq(TaskState.FAILED) and
                                        TaskFiles.state.neq(TaskState.SCANNING)
                            }
                        ) {
                            it[TaskFiles.state] = TaskState.STOPPED
                        }
                        dbTask.finishedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        _finishedAt.value = dbTask.finishedAt
                    }

                    TaskState.SCANNING -> {
                        TaskFiles.update(
                            where = {
                                TaskFiles.task.eq(dbTask.id) and
                                        TaskFiles.state.neq(TaskState.COMPLETED) and
                                        TaskFiles.state.neq(TaskState.FAILED)
                            }
                        ) {
                            it[TaskFiles.state] = TaskState.SEARCHING
                        }
                        dbTask.finishedAt = null
                        _finishedAt.value = dbTask.finishedAt
                    }

                    else -> {}
                }
            }

            if (state == TaskState.STOPPED) {
                while (true) {
                    val cnt = database.transaction {
                        TaskFiles
                            .selectAll()
                            .where {
                                TaskFiles.task.eq(dbTask.id) and
                                        TaskFiles.state.neq(TaskState.STOPPED) and
                                        TaskFiles.state.neq(TaskState.COMPLETED) and
                                        TaskFiles.state.neq(TaskState.FAILED)
                            }
                            .count()
                    }
                    if (cnt == 0L)
                        break
                    delay(1000)
                }
            }

            _busy.value = false
        }
    }

    fun stop() {
        if (_state.value != TaskState.COMPLETED)
            setState(TaskState.STOPPED)
    }

    fun resume(taskStarted: () -> Unit = {}) {
        setState(TaskState.SCANNING)
        taskStarted()
    }

    fun rescan(taskStarted: () -> Unit = {}) {
        start(rescan = true) {
            taskStarted()
        }
    }

    fun start(rescan: Boolean = false, taskStarted: () -> Unit = {}) {
        val path = dbTask.path

        taskScope.launch {
            while (_busy.value)
                delay(1000)

            val extensions = database.transaction {
                dbTask.extensions.flatMap { it.extension.extensions }
            }
            while (_state.value == TaskState.LOADING) {
                delay(500)
            }


            if (dbTask.taskState == TaskState.SEARCHING ||
                (dbTask.taskState != TaskState.PENDING && rescan)
            ) {
                database.transaction {
                    TaskFiles.deleteWhere {
                        task.eq(dbTask.id)
                    }
                }
            }
            if (_state.value == TaskState.PENDING || _state.value == TaskState.SEARCHING || rescan) {
                if (_state.value != TaskState.SEARCHING)
                    setState(TaskState.SEARCHING)

                val directorySize = scanDirectory(File(path), extensions) { item ->
                    taskScope.launch {
                        database.transaction {
                            TaskFile.new {
                                this.task = dbTask
                                this.path = item.path
                                this.state = TaskState.PENDING
                                this.size = item.length()
                            }
                        }
                    }
                }

                database.transaction {
                    dbTask.size = directorySize.filesSize.toString()
                    dbTask.filesCount = directorySize.filesCount
                }
                _totalFiles.value = directorySize.filesCount
                _folderSize.value = directorySize.filesSize.toString()
            }

            setState(TaskState.SCANNING)
            taskStarted()
        }
    }

    private fun scanDirectory(
        dir: File,
        extensions: List<String>,
        fileSelected: (file: File) -> Unit
    ): FilesCounter {
        var filesCounter = FilesCounter()

        if (dir.isDirectory) {
            val items = dir.listFiles() ?: return FilesCounter()
            for (item in items) {
                if (item.isDirectory) {
                    try {
                        filesCounter += scanDirectory(item, extensions, fileSelected)
                    } catch (_: Exception) {

                    }
                } else if (extensions.any { item.extension == it }) {
                    fileSelected(item)
                }

                filesCounter.add(item.length())
            }
        } else if (extensions.any { dir.extension == it }) {
            fileSelected(dir)
            filesCounter.add(dir.length())
        }
        return filesCounter
    }
}
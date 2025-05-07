package ru.packetdima.datascanner.scan

import androidx.lifecycle.ViewModel
import info.downdetector.bigdatascanner.common.DetectFunction
import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.and
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.db.models.*
import ru.packetdima.datascanner.scan.common.FileSize
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun

data class TaskFileResult(
    val id: Int,
    val path: String,
    val size: FileSize,
    val foundAttributes: List<IDetectFunction>,
    val count: Int,
    val score: Long
)

class TaskFilesViewModel(val task: Task) : KoinComponent, ViewModel(){
    private val database: DatabaseConnector by inject()

    private val taskScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val _taskFiles = MutableStateFlow<List<TaskFileResult>>(listOf())
    val taskFiles
        get() = _taskFiles.asStateFlow()

    private val _scoreSum = MutableStateFlow(0L)
    val scoreSum
        get() = _scoreSum.asStateFlow()

    init {
        update()
    }

    fun update() {
        taskScope.launch {
            database.transaction {
                val fileRows = TaskFiles
                    .innerJoin(TaskFileScanResults)
                    .select(
                        TaskFiles.path,
                        TaskFiles.size,
                        TaskFiles.id
                    )
                    .where {
                        TaskFiles.task.eq(task.id) and TaskFiles.state.eq(TaskState.COMPLETED)
                    }
                    .withDistinct()

                _taskFiles.value = fileRows.map { fileRow ->
                    val detectRows = TaskFileScanResults
                        .innerJoin(TaskDetectFunctions)
                        .select(TaskDetectFunctions.function, TaskFileScanResults.count)
                        .where { TaskFileScanResults.file.eq(fileRow[TaskFiles.id]) }
                        .map { it[TaskDetectFunctions.function] to it[TaskFileScanResults.count] }

                    val containsFIO = detectRows.map { it.first }.contains(DetectFunction.Name)

                    TaskFileResult(
                        id = fileRow[TaskFiles.id].value,
                        path = fileRow[TaskFiles.path],
                        size = FileSize(fileRow[TaskFiles.size]),
                        foundAttributes = detectRows.map { it.first },
                        count = detectRows.sumOf { it.second },
                        score = (if(containsFIO) 20 else 0 + detectRows.size) * detectRows.sumOf { row ->
                            (when(row.first) {
                                DetectFunction.Name -> 5f
                                DetectFunction.CardNumbers -> 30f
                                DetectFunction.AccountNumber -> 30f
                                CodeDetectFun -> 0.01f
                                CertDetectFun -> 100f
                                else -> 1f
                            } * row.second).toLong()
                        }
                    )
                }
                _scoreSum.value = _taskFiles.value.sumOf { it.score.toLong() }

            }
        }

    }
}
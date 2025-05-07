package ru.packetdima.datascanner.searcher

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.FileSize
import ru.packetdima.datascanner.scan.common.files.FileType
import java.io.File
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

class SensitiveSearcher: KoinComponent {

    private var completedCount: Long = 0
    private val completedFilesSize: FileSize = FileSize()
    private var totalCount: Int = 0
    private var threadsCompleted = 0
    private var totalThreadsCount = 0


    suspend fun inspectDirectory(
        directory: File,
        onProgressChange: (progress: Pair<Long, FileSize>) -> Unit,
        onFileFound: (size: Long) -> Unit,
        onSkipSelectFile: (fileSize: Long) -> Unit,
        onSkipScanFile: (fileSize: Long) -> Unit,
        onReportCreated: (filesCount: Pair<Long, FileSize>) -> Unit,
    ) = coroutineScope {
        val scanSettings: ScanSettings by inject()
        val appSettings: AppSettings by inject()
        try {
            var foundFiles = 0
            val files = scanDirectory(directory,
                FileType.entries.filter { scanSettings.extensions.contains(it) }.flatMap { it.extensions },
                onSkippedFile = onSkipSelectFile,
                onFileFound = {
                    foundFiles++
                    onFileFound(it)
                })
            var partLength = (files.count() / appSettings.threadCount.value) / 10
            if (partLength > 128) partLength = 128
            else if (partLength < 8) partLength = 1

            val partsCount = files.size / partLength

            val queue = Array(partsCount) {
                val res = mutableListOf<File>()
                for (i in 0 until partLength) {
                    res.add(files[it + i * partsCount])
                }
                Pair(it, res)
            }

            totalCount = queue.sumOf { it.second.size }
            totalThreadsCount = queue.size

            val dispatcher = Executors.newFixedThreadPool(appSettings.threadCount.value).asCoroutineDispatcher()

            onProgressChange(completedCount to completedFilesSize)

            val futures = queue.map {
                CoroutineScope(dispatcher).async {
                    if (!isActive)
                        return@async
                    val iterator = it.second.iterator()
                    val report = mutableListOf<Document>()
                    while (isActive && iterator.hasNext()) {
                        val file = iterator.next()
                        FileType.getFileType(file)?.scanFile(file, currentCoroutineContext(), scanSettings.detectFunctions, scanSettings.fastScan.value)?.let {
                            report.add(it)
                            if (it.skipped()) onSkipScanFile(it.size)
                            else {
                                completedCount++
                                completedFilesSize.plus(it.size)
                                onProgressChange(completedCount to completedFilesSize)
                            }
                        }
                    }
                    Writer.write(report)
                    threadsCompleted++
                }
            }
            futures.awaitAll()

            //Передаем статус о том, что начата генерация отчета
            onProgressChange(Pair(-1L, FileSize()))

            onReportCreated(SearcherResult.createResult())

        } catch (e: Exception) {
            logger.error { e.message }
            logger.error { e.stackTraceToString() }
        }
    }

    private fun scanDirectory(
        dir: File,
        extensions: List<String>,
        onFileFound: (fileSize: Long) -> Unit,
        onSkippedFile: (fileSize: Long) -> Unit
    ): List<File> {
        val res = mutableListOf<File>()
        if (dir.isDirectory) {
            val items = dir.listFiles() ?: return listOf()
            for (item in items) {
                if (item.isDirectory) {
                    try {
                        res.addAll(scanDirectory(item, extensions, onFileFound, onSkippedFile))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (extensions.any { ext -> item.extension == ext }) {
                    res.add(item)
                    onFileFound(item.length())
                } else {
                    onSkippedFile(item.length())
                }
            }
            return res
        } else if (extensions.any { ext -> dir.extension == ext }) {
            onFileFound(dir.length())
            return listOf(dir)
        } else return listOf()
    }
}
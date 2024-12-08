package ru.packetdima.datascanner

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tongfei.progressbar.ProgressBar
import ru.packetdima.datascanner.misc.FilesCounter
import ru.packetdima.datascanner.searcher.SensitiveSearcher
import ru.packetdima.datascanner.searcher.Writer
import java.io.File
import kotlin.system.exitProcess

object Console {
    lateinit var progressBar: ProgressBar

    suspend fun consoleRun(args: Array<String>) {

        fun getArg(map: Map<String, List<String>>, tag: String, default: String?): String? = map[tag]?.first() ?: default
        @Suppress("Unused")
        fun getArg(map: Map<String, List<String>>, tag: String, default: Boolean): Boolean =
            if (map.containsKey(tag)) !default else default

        val map = args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
            if (elem.startsWith("-")) Pair(map + (elem to emptyList()), elem)
            else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
        }.first

        val searcher = SensitiveSearcher()

        val path = getArg(map, "-path", null)

        var scanStarted = false
        val filesCounter = FilesCounter()

        if (path != null) {
            println("Selected directory: $path")
            println("Selecting files...")
            Writer.initDB()
            val job = CoroutineScope(Dispatchers.Default).launch {
                searcher.inspectDirectory(
                    File(path.trim('"')),
                    onProgressChange = {
                        if (it.first >= 0) {
                            if(!scanStarted) {
                                progressBar = ProgressBar("Scanning", filesCounter.selectedFilesCount)
                                scanStarted = true
                            }
                            filesCounter.scannedFilesCount = it.first
                            filesCounter.scannedFilesSize = it.second

                            progressBar.stepTo(filesCounter.scannedFilesCount + filesCounter.skippedScanFilesCount)
                        } else {
                            scanStarted = false
                        }
                    },
                    onFileFound = {
                        filesCounter.selectedFilesCount++
                        filesCounter.selectedFilesSize += it

                        filesCounter.totalFilesCount++
                        filesCounter.totalFilesSize += it
                    },
                    onSkipSelectFile = {
                        filesCounter.totalFilesSize += it
                        filesCounter.totalFilesCount++
                    },
                    onSkipScanFile = {
                        filesCounter.skippedScanFilesCount++
                        filesCounter.skippedScanFilesSize += it
                        progressBar.stepTo(filesCounter.scannedFilesCount + filesCounter.skippedScanFilesCount)
                    },
                    onReportCreated = {
                        filesCounter.valuebleFilesCount = it.first
                        filesCounter.valuebleFilesSize = it.second
                    }
                )
            }
            job.join()

            delay(1000)
            println("\nReport created")
            println("Valuable file found: ${filesCounter.valuebleFilesCount} with size ${filesCounter.valuebleFilesSize}")
            println("Program completed")
            exitProcess(0)
        } else
            println("Chose path to scan with -path parameter")
    }
}

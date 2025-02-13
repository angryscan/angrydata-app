package ru.packetdima.datascanner

import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.coroutines.*
import me.tongfei.progressbar.ProgressBar
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.misc.FilesCounter
import ru.packetdima.datascanner.searcher.FileType
import ru.packetdima.datascanner.searcher.SensitiveSearcher
import ru.packetdima.datascanner.searcher.Writer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

object Console {
    lateinit var progressBar: ProgressBar

    private var path: String? = null
    private var reportDir: File = AppFiles.WorkDir
    private var reportEncoding = "UTF-8"

    suspend fun consoleRun(args: Array<String>) {

        val searcher = SensitiveSearcher()

        var scanStarted = false
        val filesCounter = FilesCounter()

        parseArgs(args)

        if (path != null) {
            println("Selecting files...")
            Writer.initDB()
            val job = CoroutineScope(Dispatchers.Default).launch {
                searcher.inspectDirectory(
                    File(path!!.trim('"')),
                    onProgressChange = {
                        if (it.first >= 0) {
                            if (!scanStarted) {
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
            saveReport()

            println("Program completed")
            exitProcess(0)
        } else
            println("Chose path to scan with -path parameter")
    }

    private fun saveReport() {
        println("Saving report...")
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        val currentTime = formatter.format(time)
        val reportFile = reportDir.resolve("ADS_$currentTime.csv")
        Writer.writeReport(reportFile, reportEncoding)

        println("Report saved to $reportFile")
    }

    private fun parseArgs(args: Array<String>) {
        fun getArg(map: Map<String, List<String>>, tag: String, default: String?): String? =
            map[tag]?.first() ?: default

        fun getArg(map: Map<String, List<String>>, tag: String, default: Boolean): Boolean =
            if (map.containsKey(tag)) !default else default

        fun getArg(map: Map<String, List<String>>, tag: String, default: Int): Int =
            map[tag]?.first()?.toIntOrNull() ?: default

        val map = args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
            if (elem.startsWith("-")) Pair(map + (elem to emptyList()), elem)
            else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
        }.first

        if (map.containsKey("-fast") && map.containsKey("-full")) {
            println("You can't use both -fast and -full")
            exitProcess(1)
        }

        path = getArg(map, "-path", null)
        val fileExtensions = getArg(map, "-extensions", Settings.searcher.extensions.joinToString(","))
        val detectFunctions = getArg(map, "-detect", Settings.searcher.detectFunctions.joinToString(","))
        val userSignatures = getArg(map, "-usig_detect", Settings.userFunctionLoader.userSignature.joinToString(","))
        val fastScan = getArg(map, "-fast", Settings.searcher.fastScan.value)
        val fullScan = getArg(map, "-full", !fastScan)
        val threadCount = getArg(map, "threads", Settings.searcher.threadCount.value)
        val reportPath = getArg(map, "-report", null)
        val encoding = getArg(map, "-report_encoding", null)

        if(encoding != null) {
            reportEncoding = encoding
            println("Report encoding: $reportEncoding")
        }

        if(path == null) {
            println("Chose path to scan with -path parameter")
            exitProcess(1)
        } else {
            println("Selected directory: $path")
        }

        if (reportPath != null) {
            val rpFile = File(reportPath)
            if (rpFile.exists()) {
                if (!rpFile.isDirectory)
                    reportDir = rpFile
                else {
                    println("Report path must be a directory")
                    exitProcess(1)
                }
            } else {
                if (rpFile.mkdirs())
                    reportDir = rpFile
                else {
                    println("Can't create report directory")
                    exitProcess(1)
                }
            }
            println("Report dir: ${reportDir.absolutePath}")
        } else {
            println("Report path not specified, using default: ${reportDir.absolutePath}")
        }

        Settings.searcher.fastScan.value = !fullScan
        if(Settings.searcher.fastScan.value)
            println("Fast scan enabled")
        else
            println("Full scan enabled")
        if(threadCount > Runtime.getRuntime().availableProcessors()){
            println("Thread count can't be greater than available processors (${Runtime.getRuntime().availableProcessors()})")
            println("Using ${Runtime.getRuntime().availableProcessors()} instead")
            Settings.searcher.threadCount.value = Runtime.getRuntime().availableProcessors()
        } else if (threadCount < 1) {
            println("Thread count can't be less than 1")
            println("Using 1 instead")
            Settings.searcher.threadCount.value = 1
        } else {
            println("Thread count: $threadCount. Max: ${Runtime.getRuntime().availableProcessors()}")
            Settings.searcher.threadCount.value = threadCount
        }

        if (fileExtensions != null) {
            Settings.searcher.extensions.clear()
            fileExtensions.split(",").forEach { ext ->
                if (FileType.entries.map { it.name }.contains(ext))
                    Settings.searcher.extensions.add(ext)
                else
                    println("Unknown file extension: $ext, skipping...")
            }
        }
        println("Extensions: ${Settings.searcher.extensions.joinToString(", ")}")

        if (detectFunctions != null) {
            Settings.searcher.detectFunctions.clear()
            detectFunctions.split(",").forEach { df ->
                val dfo = DetectFunction.entries.find { it.name == df }
                if (dfo != null)
                    Settings.searcher.detectFunctions.add(dfo)
                else
                    println("Unknown detect function: $df, skipping...")
            }
        }
        println("Detect functions: ${Settings.searcher.detectFunctions.joinToString(", ")}")

        if(userSignatures != null) {
            Settings.searcher.userSignature.clear()
            userSignatures.split(",").forEach { sig ->
                val sigo = Settings.userFunctionLoader.userSignature.find { it.name == sig }
                if (sigo != null)
                    Settings.searcher.userSignature.add(sigo)
                else
                    println("Unknown user detect signature: $sig, skipping...")
            }
        }
        println("User signature functions: ${Settings.searcher.userSignature.joinToString(", ")}")
    }

    fun help() {
        println(
            """
Allowed parameters:
-path [path] - path to scan
-extensions [extensions] - comma-separated list of file extensions
-detect [detect functions] - comma-separated list of detect functions
-usig_detect [user detect signatures] - comma-separated list of user detect signatures
-fast - fast scan
-full - full scan
-console - console mode
-report [path] - path to dir to save report
-threads [count] - count of threads
-report_encoding [encoding] - report encoding (UTF-8, Windows-1251) (default: UTF-8)

Allowed extensions: 
        ${
                FileType.entries.joinToString("\n        ") {
                    "${it.name} (${
                        it.extensions.filter { ext -> ext.toIntOrNull() == null }.joinToString(",")
                    })"
                }
            }

Allowed detect functions: 
        ${DetectFunction.entries.joinToString("\n        ") { "${it.name} (${it.writeName})" }}
Allowed user detect signatures:
        ${Settings.userFunctionLoader.userSignature.joinToString("\n        ") { "${it.name} (${it.writeName})" }} 
            """.trimIndent()
        )
    }
}

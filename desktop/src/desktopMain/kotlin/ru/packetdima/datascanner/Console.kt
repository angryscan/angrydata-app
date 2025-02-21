package ru.packetdima.datascanner

import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.coroutines.*
import me.tongfei.progressbar.ProgressBar
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.scan.common.FileType
import ru.packetdima.datascanner.searcher.ConsoleFilesCounter
import ru.packetdima.datascanner.searcher.SensitiveSearcher
import ru.packetdima.datascanner.searcher.Writer
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

object Console: KoinComponent {
    lateinit var progressBar: ProgressBar

    private var path: String? = null
    private var reportDir: File = AppFiles.WorkDir
    private var reportEncoding = "UTF-8"

    suspend fun consoleRun(args: Array<String>) {

        val searcher = SensitiveSearcher()

        var scanStarted = false
        val filesCounter = ConsoleFilesCounter()

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

        val scanSettings: ScanSettings by inject()
        val appSettings: AppSettings by inject()

        path = getArg(map, "-path", null)
        val fileExtensions = getArg(map, "-extensions", scanSettings.extensions.joinToString(","))
        val detectFunctions = getArg(map, "-detect", scanSettings.detectFunctions.joinToString(","))
        val userSignatures = getArg(map, "-usig_detect", scanSettings.userSignature.joinToString(","))
        val fastScan = getArg(map, "-fast", scanSettings.fastScan.value)
        val fullScan = getArg(map, "-full", !fastScan)
        val threadCount = getArg(map, "threads", appSettings.threadCount.value)
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

        scanSettings.fastScan.value = !fullScan
        if(scanSettings.fastScan.value)
            println("Fast scan enabled")
        else
            println("Full scan enabled")
        if(threadCount > Runtime.getRuntime().availableProcessors()){
            println("Thread count can't be greater than available processors (${Runtime.getRuntime().availableProcessors()})")
            println("Using ${Runtime.getRuntime().availableProcessors()} instead")
            appSettings.threadCount.value = Runtime.getRuntime().availableProcessors()
        } else if (threadCount < 1) {
            println("Thread count can't be less than 1")
            println("Using 1 instead")
            appSettings.threadCount.value = 1
        } else {
            println("Thread count: $threadCount. Max: ${Runtime.getRuntime().availableProcessors()}")
            appSettings.threadCount.value = threadCount
        }

        if (fileExtensions != null) {
            scanSettings.extensions.clear()
            fileExtensions.split(",").forEach { ext ->
                val extension = FileType.entries.find { it.name == ext }
                if (extension != null)
                    scanSettings.extensions.add(extension)
                else
                    println("Unknown file extension: $ext, skipping...")
            }
        }
        println("Extensions: ${scanSettings.extensions.joinToString(", ")}")

        if (detectFunctions != null) {
            scanSettings.detectFunctions.clear()
            detectFunctions.split(",").forEach { df ->
                val dfo = DetectFunction.entries.find { it.name == df }
                if (dfo != null)
                    scanSettings.detectFunctions.add(dfo)
                else
                    println("Unknown detect function: $df, skipping...")
            }
        }
        println("Detect functions: ${scanSettings.detectFunctions.joinToString(", ")}")

        if(userSignatures != null) {
            scanSettings.userSignature.clear()
            userSignatures.split(",").forEach { sig ->
                val sigo = scanSettings.userSignature.find { it.name == sig }
                if (sigo != null)
                    scanSettings.userSignature.add(sigo)
                else
                    println("Unknown user detect signature: $sig, skipping...")
            }
        }
        println("User signature functions: ${scanSettings.userSignature.joinToString(", ")}")
    }

    fun help() {
        val scanSettings: ScanSettings by inject()
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
        ${scanSettings.userSignature.joinToString("\n        ") { "${it.name} (${it.writeName})" }} 
            """.trimIndent()
        )
    }
}

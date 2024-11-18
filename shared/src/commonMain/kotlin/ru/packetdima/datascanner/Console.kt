package ru.packetdima.datascanner

import ru.packetdima.datascanner.misc.FileSize
import ru.packetdima.datascanner.searcher.SensitiveSearcher
import java.io.File

suspend fun consoleRun(args: Array<String>, onProgressChange: (progress: Pair<Long, FileSize>) -> Unit) {

    fun getArg(map: Map<String, List<String>>, tag: String, default: String): String = map[tag]?.first() ?: default
    fun getArg(map: Map<String, List<String>>, tag: String, default: Boolean): Boolean =
        if (map.containsKey(tag)) !default else default

    val map = args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
        if (elem.startsWith("-")) Pair(map + (elem to emptyList()), elem)
        else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
    }.first

    val searcher = SensitiveSearcher()

    if (getArg(map, "-dir", false)) {
        searcher.inspectDirectory(
            File(getArg(map, "-path", "C:\\").trim('"')),
            onProgressChange = onProgressChange,
            onSkipScanFile = {},
            onFileFound = {},
            onSkipSelectFile = {},
            onReportCreated = {}
        )
        println("Program completed")
    } else
        println("Chose to inspect database (-db) or directory (-dir) with path (-path)")
}
package ru.packetdima.datascanner.common

import ru.packetdima.datascanner.misc.OS
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute

@Suppress("Unused")
object AppFiles {
    private val WorkDirPath: Path = when (OS.currentOS()) {
        OS.WINDOWS -> Path(System.getenv("LOCALAPPDATA")).resolve("BigDataScanner")
        else -> Path(System.getProperty("user.home")).resolve(".ads")
    }

    val WorkDir = WorkDirPath.toFile().also { path ->
        if (!path.exists() && !path.mkdir())
            throw Exception("Fail to create application directory")
        if (!path.isDirectory)
            throw Exception("Path ${WorkDirPath.absolute()} exists and it's not directory")
        path.absoluteFile
    } ?: throw Exception("Fail to create application directory")

    val LoggingDir: Path = WorkDirPath.resolve("logs")

    val ResultDBFile: File = WorkDirPath.resolve("result.db").toFile()
    val SearchSettingsFile: File = WorkDirPath.resolve("properties.json").toFile()
    val UISettingsFile: File = WorkDirPath.resolve("ui.json").toFile()
    val UserFunctionsFile: File = WorkDirPath.resolve("functions.json").toFile()
}
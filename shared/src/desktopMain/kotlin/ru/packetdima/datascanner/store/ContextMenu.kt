package ru.packetdima.datascanner.store

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.OS
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.contextScanWith
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.math.log


private val logger = KotlinLogging.logger { }

object ContextMenu {

    var enabled: Boolean
        get() = when (OS.currentOS()) {
            OS.WINDOWS -> {
                WinRegistry.getWindowsRegistryEntry(
                    "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\bds",
                    "MUIVerb",
                    type = WinRegistry.REG_TYPE.REG_SZ
                )?.isNotBlank() ?: false
            }

            OS.LINUX -> {
                val nautilusPath = Path(System.getProperty("user.home")).resolve(".local/share/nautilus/scripts")
                val file = runBlocking {
                    getString(Res.string.contextScanWith)
                }.let { f ->
                    nautilusPath.resolve(f).toFile()
                }
                file.exists()
            }

            else -> false
        }
        set(value) {
            if (value)
                enable()
            else
                disable()

        }

    fun supported(): Boolean = supported(OS.currentOS())
    fun supported(os: OS) = when (os) {
        OS.WINDOWS -> true
        OS.LINUX -> true
        else -> false
    }


    @OptIn(ExperimentalResourceApi::class)
    private fun enable() {
        when (OS.currentOS()) {
            OS.WINDOWS -> {
                val icon = runBlocking {
                    Res.readBytes("files/icon.ico")
                }

                AppFiles.Icon.writeBytes(icon)

                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\bds",
                        "MUIVerb",
                        runBlocking { getString(Res.string.contextScanWith) },
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key Directory\\shell\\bds MUIVerb" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\bds",
                        "Icon",
                        AppFiles.Icon.absolutePath,
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key Directory\\shell\\bds Icon" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\bds\\command",
                        "\\\"${System.getProperty("user.dir")}\\Big Data Scanner.exe\\\" \\\"%1\\\"",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key Directory\\shell\\bds\\command" }

                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\bds",
                        "MUIVerb",
                        runBlocking { getString(Res.string.contextScanWith) },
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key *\\shell\\bds MUIVerb" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\bds",
                        "Icon",
                        AppFiles.Icon.absolutePath,
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key  *\\shell\\bds Icon" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\bds\\command",
                        "\\\"${System.getProperty("user.dir")}\\Big Data Scanner.exe\\\" \\\"%1\\\"",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key   *\\shell\\bds\\command" }
            }

            OS.LINUX -> {
                try {


                    val nautilusPath = Path(System.getProperty("user.home")).resolve(".local/share/nautilus/scripts")
                    val file = runBlocking {
                        getString(Res.string.contextScanWith)
                    }.let { f ->
                        nautilusPath.resolve(f).toFile()
                    }

                    val appPath = Path(System.getProperty("user.dir"))
                    val executable = if (appPath.resolve("bin").exists())
                        appPath.resolve("bin").resolve("Big Data Scanner")
                    else
                        appPath.resolve("Big Data Scanner")

                    file.writeText(
                        "#!/bin/bash \n" +
                                "${
                                    executable.toAbsolutePath().toString().replace(" ", "\\ ")
                                } \${NAUTILUS_SCRIPT_SELECTED_FILE_PATHS}"
                    )

                    Files.setPosixFilePermissions(
                        file.toPath(),
                        setOf(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OTHERS_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.OWNER_EXECUTE
                        )
                    )
                } catch (_: Exception) {
                    logger.error { "Failed to enable context menu" }
                }
            }

            else -> throw IllegalStateException("Unsupported OS")
        }
    }

    private fun disable() {
        when (OS.currentOS()) {
            OS.WINDOWS -> {
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\bds\\command",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key Directory\\shell\\bds\\command" }
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\bds",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key Directory\\shell\\bds" }
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\bds\\command",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key *\\shell\\bds\\command" }
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\bds",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key *\\shell\\bds" }
            }

            OS.LINUX -> {
                val nautilusPath = Path(System.getProperty("user.home")).resolve(".local/share/nautilus/scripts")
                val file = runBlocking {
                    getString(Res.string.contextScanWith)
                }.let { f ->
                    nautilusPath.resolve(f).toFile()
                }
                if (!file.delete()) {
                    logger.error { "Failed to disable context menu!" }
                }
            }

            else -> throw IllegalStateException("Unsupported OS")
        }
    }
}
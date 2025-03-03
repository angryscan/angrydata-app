package ru.packetdima.datascanner.store

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import ru.packetdima.datascanner.common.OS
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.contextScanWith


private val logger = KotlinLogging.logger { }

object ContextMenu {
    
    var enabled: Boolean
        get() = when (OS.currentOS()) {
            OS.WINDOWS -> {
                WinRegistry.getWindowsRegistryEntry(
                    "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\ads",
                    "MUIVerb",
                    type = WinRegistry.REG_TYPE.REG_SZ
                )?.isNotBlank() ?: false
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
        else -> false
    }

    private fun enable() {
        when (OS.currentOS()) {
            OS.WINDOWS -> {
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\ads",
                        "MUIVerb",
                        runBlocking { getString(Res.string.contextScanWith) },
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key Directory\\shell\\ads MUIVerb" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\ads",
                        "Icon",
                        "${System.getProperty("user.dir")}\\Big Data Scanner.ico",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key Directory\\shell\\ads Icon" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\ads\\command",
                        "\\\"${System.getProperty("user.dir")}\\Big Data Scanner.exe\\\" \\\"%1\\\"",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key Directory\\shell\\ads\\command" }

                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\ads",
                        "MUIVerb",
                        runBlocking { getString(Res.string.contextScanWith) },
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key *\\shell\\ads MUIVerb" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\ads",
                        "Icon",
                        "${System.getProperty("user.dir")}\\Big Data Scanner.ico",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key  *\\shell\\ads Icon" }
                if (!WinRegistry.setWindowsRegistryEntry(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\ads\\command",
                        "\\\"${System.getProperty("user.dir")}\\Big Data Scanner.exe\\\" \\\"%1\\\"",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to set registry key   *\\shell\\ads\\command" }
            }

            else -> throw IllegalStateException("Unsupported OS")
        }
    }

    private fun disable() {
        when (OS.currentOS()) {
            OS.WINDOWS -> {
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\ads\\command",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key Directory\\shell\\ads\\command" }
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\Directory\\shell\\ads",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key Directory\\shell\\ads" }
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\ads\\command",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key *\\shell\\ads\\command" }
                if (!WinRegistry.deleteWindowsRegistryKey(
                        "HKEY_CURRENT_USER\\Software\\Classes\\*\\shell\\ads",
                        type = WinRegistry.REG_TYPE.REG_SZ
                    )
                ) logger.error { "Failed to delete registry key *\\shell\\ads" }
            }

            else -> throw IllegalStateException("Unsupported OS")
        }
    }
}
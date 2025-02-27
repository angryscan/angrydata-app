package ru.packetdima.datascanner.common

import androidx.compose.runtime.mutableStateListOf
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.scan.functions.UserSignature
import java.io.File

@Serializable
class UserSignatureSettings: KoinComponent {
    @Transient
    private val logger = KotlinLogging.logger {  }
    class SettingsFile(path: String): File(path)

    private val settingsFile: SettingsFile by inject()

    @Serializable
    var userSignatures: MutableList<UserSignature> = mutableStateListOf()

    constructor() {
        try {
            val prop: ScanSettings = Json.decodeFromString(settingsFile.readText())
            this.userSignatures = prop.userSignatures
        } catch (_: Exception) {
            logger.error {
                "Failed to load User signatures"
            }
        }
    }

    fun save() {
        settingsFile.writeText(Json.encodeToString(this))
    }
}
package ru.packetdima.datascanner.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import info.downdetector.bigdatascanner.common.DetectFunction
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.scan.common.FileType
import ru.packetdima.datascanner.scan.functions.UserSignature
import ru.packetdima.datascanner.serializers.MutableStateSerializer
import java.io.File

@Serializable
class ScanSettings: KoinComponent {
    @Transient
    private val logger = KotlinLogging.logger{}
    class SettingsFile(path: String): File(path)

    private val settingsFile: SettingsFile by inject()

    @Serializable
    val extensions: MutableList<FileType> = mutableStateListOf()
    @Serializable(with = MutableStateSerializer::class)
    var extensionsExpanded: MutableState<Boolean>

    @Serializable
    val detectFunctions: MutableList<DetectFunction> = mutableStateListOf()
    @Serializable(with = MutableStateSerializer::class)
    var detectFunctionsExpanded: MutableState<Boolean>

    @Serializable
    val userSignatures: MutableList<UserSignature> = mutableStateListOf()
    @Serializable(with = MutableStateSerializer::class)
    var userSignatureExpanded: MutableState<Boolean>

    @Serializable(with = MutableStateSerializer::class)
    var fastScan: MutableState<Boolean>
    val sampleLength = 10_000
    val sampleCount = 100

    constructor() {
        val userSignatureSettings by inject<UserSignatureSettings>()
        try {
            val prop: ScanSettings = Json.decodeFromString(settingsFile.readText())

            this.extensions.addAll(prop.extensions)
            this.extensionsExpanded = prop.extensionsExpanded

            this.fastScan = prop.fastScan

            this.detectFunctions.addAll(prop.detectFunctions)
            this.detectFunctionsExpanded = prop.detectFunctionsExpanded

            this.userSignatures.addAll(prop.userSignatures.filter { it in userSignatureSettings.userSignatures })
            this.userSignatureExpanded = prop.userSignatureExpanded
        } catch (ex: Exception) {
            logger.error {
                "Failed to load ScanSettings. Loading default."
            }
            this.extensions.clear()
            this.extensions.addAll(FileType.entries.filter { it != FileType.ZIP && it != FileType.RAR })
            this.extensionsExpanded = mutableStateOf(false)
            this.detectFunctions.clear()
            this.detectFunctions.addAll(DetectFunction.entries.toTypedArray())
            this.detectFunctionsExpanded = mutableStateOf(false)
            this.fastScan = mutableStateOf(false)
            this.userSignatureExpanded = mutableStateOf(false)
        }
    }

    fun save() {
        settingsFile.writeText(Json.encodeToString(this))
    }
}
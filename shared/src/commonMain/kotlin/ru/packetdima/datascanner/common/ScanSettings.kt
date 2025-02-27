package ru.packetdima.datascanner.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.scan.functions.UserSignature
import ru.packetdima.datascanner.scan.common.FileType
import ru.packetdima.datascanner.serializers.MutableStateSerializer
import java.io.File

@Serializable
class ScanSettings: KoinComponent {
    class ScanSettingsFile(path: String): File(path)

    private val settingsFile: ScanSettingsFile by inject()

    val extensions: MutableList<FileType> = mutableListOf()
    val detectFunctions: MutableList<DetectFunction> = mutableListOf()

    @Serializable
    val userSignature: MutableList<UserSignature> = mutableListOf()

    @Serializable(with = MutableStateSerializer::class)
    var fastScan: MutableState<Boolean>
    val sampleLength = 10_000
    val sampleCount = 100

    constructor() {
        try {
            val prop: ScanSettings = Json.decodeFromString(settingsFile.readText())

            this.extensions.addAll(prop.extensions)

            this.fastScan = prop.fastScan

            this.detectFunctions.addAll(prop.detectFunctions)

            this.userSignature.addAll(prop.userSignature)
        } catch (ex: Exception) {
            this.extensions.clear()
            this.extensions.addAll(FileType.entries.filter { it != FileType.ZIP && it != FileType.RAR })
            this.detectFunctions.clear()
            this.detectFunctions.addAll(DetectFunction.entries.toTypedArray())
            this.fastScan = mutableStateOf(false)
        }
    }

    fun save() {
        settingsFile.writeText(Json.encodeToString(this))
    }
}
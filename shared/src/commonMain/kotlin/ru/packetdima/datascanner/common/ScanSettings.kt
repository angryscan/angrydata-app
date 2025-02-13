package ru.packetdima.datascanner.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.searcher.FileType
import ru.packetdima.datascanner.serializers.MutableStateSerializer
import java.io.File

@Serializable
class ScanSettings: KoinComponent {
    class ScanSettingsFile(path: String): File(path)

    private val settingsFile: ScanSettingsFile by inject()

    val extensions: MutableList<String> = mutableListOf()
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


            if (!FileType.entries.map { it.name }.containsAll(prop.extensions))
                throw Exception("Not all available")

            this.extensions.addAll(prop.extensions)

            this.fastScan = prop.fastScan

            this.detectFunctions.addAll(prop.detectFunctions)

            this.userSignature.addAll(prop.userSignature)
        } catch (ex: Exception) {
            this.extensions.clear()
            this.extensions.addAll(FileType.entries.map { it.name }.filter { it != "ZIP" })
            this.detectFunctions.clear()
            this.detectFunctions.addAll(DetectFunction.entries.toTypedArray())
            this.fastScan = mutableStateOf(false)
        }
    }

    fun save(file: File) {
        file.writeText(Json.encodeToString(this))
    }
}
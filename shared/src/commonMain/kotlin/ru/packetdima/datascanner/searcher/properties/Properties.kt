package ru.packetdima.datascanner.searcher.properties

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import info.downdetector.bigdatascanner.common.DetectFunction
import ru.packetdima.datascanner.common.UserSignature
import ru.packetdima.datascanner.searcher.FileType
import ru.packetdima.datascanner.serializers.MutableStateSerializer
import java.io.File
import kotlin.math.max
import kotlin.math.min


@Serializable
class Properties : java.io.Serializable {
    val extensions: MutableList<String> = mutableListOf()
    val detectFunctions: MutableList<DetectFunction> = mutableListOf()
    @Serializable
    val userSignature: MutableList<UserSignature> = mutableListOf()

    @Serializable(with = MutableStateSerializer::class)
    var threadCount: MutableState<Int>

    @Serializable(with = MutableStateSerializer::class)
    var fastScan: MutableState<Boolean>
    val sampleLength = 10_000
    val sampleCount = 100

    constructor(propertiesFile: File) {
        try {
            val prop = Json.decodeFromString<Properties>(propertiesFile.readText())

            this.threadCount = mutableStateOf(
                min(
                    prop.threadCount.value,
                    Runtime.getRuntime().availableProcessors()
                )
            )

            if (!FileType.entries.map { it.name }.containsAll(prop.extensions))
                throw Exception("Not all available")

            this.extensions.addAll(prop.extensions)

            this.fastScan = prop.fastScan

            this.detectFunctions.addAll(prop.detectFunctions)

            this.userSignature.addAll(prop.userSignature)
        } catch (ex: Exception) {
            this.threadCount = mutableStateOf(
                max(
                    (Runtime.getRuntime().availableProcessors() - 1) / 2,
                    1
                )
            )
            this.extensions.clear()
            this.extensions.addAll(FileType.entries.map { it.name }.filter { it != "ZIP" })
            this.detectFunctions.clear()
            this.detectFunctions.addAll(DetectFunction.entries.toTypedArray())
            this.fastScan = mutableStateOf(false)
        }
    }

    constructor(propertiesFilePath: String) : this(File(propertiesFilePath))

    fun save(file: File) {
        file.writeText(Json.encodeToString(this))
    }
}




package ru.packetdima.datascanner.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import ru.packetdima.datascanner.serializers.MutableStateSerializer
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.default
import java.io.File
import java.util.*

@Serializable
class UIProperties {
    enum class ThemeType {
        Dark,
        Light,
        System
    }

    @Suppress("Unused")
    enum class LanguageType(val text: String, val locale: String) {
        Russian(
            text = "Русский",
            locale = "ru"
        ),
        English(
            text = "English",
            locale = "en"
        ),

        @OptIn(ExperimentalResourceApi::class)
        Default(
            text = runBlocking { getString(Res.string.default) },
            locale = when (Locale.getDefault().language) {
                "en" -> "en"
                "ru" -> "ru"
                else -> "en"
            }
        )
    }

    @Serializable(with = MutableStateSerializer::class)
    var theme: MutableState<ThemeType>
    var propertiesList: MutableSet<String>

    @Serializable(with = MutableStateSerializer::class)
    var language: MutableState<LanguageType>

    constructor(propertiesFile: File) {
        try {
            val prop = Json.decodeFromString<UIProperties>(propertiesFile.readText())
            this.theme = prop.theme
            this.propertiesList = prop.propertiesList
            this.language = prop.language

        } catch (ex: Exception) {
            this.theme = mutableStateOf(ThemeType.Dark)
            this.propertiesList = mutableSetOf()
            this.language = mutableStateOf(LanguageType.Default)
        }
    }

    constructor(propertiesFilePath: String) : this(File(propertiesFilePath))

    fun save(file: File) {
        file.writeText(Json.encodeToString(this))
    }
}
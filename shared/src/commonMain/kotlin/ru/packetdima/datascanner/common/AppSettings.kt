package ru.packetdima.datascanner.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.serializers.MutableStateSerializer
import java.io.File
import java.util.*
import kotlin.math.max

@Serializable
class AppSettings : KoinComponent {
    @Transient
    private val logger = KotlinLogging.logger {  }
    class AppSettingsFile(path: String) : File(path)

    enum class ThemeType {
        Dark,
        Light,
        System
    }

    enum class LanguageType(val text: String, val locale: String) {
        RU(
            text = "Русский",
            locale = "ru"
        ),
        EN(
            text = "English",
            locale = "en"
        ),
        Default(
            text = when (Locale.getDefault().language) {
                "ru" -> "Русский"
                "en" -> "English"
                else -> "Русский"
            },
            locale = when (Locale.getDefault().language) {
                "ru" -> "ru"
                "en" -> "en"
                else -> "ru"
            }
        )
    }

    private val settingsFile: AppSettingsFile by inject()

    @Serializable(with = MutableStateSerializer::class)
    var threadCount: MutableState<Int>

    @Serializable(with = MutableStateSerializer::class)
    var theme: MutableState<ThemeType>
    var propertiesList: MutableSet<String>

    @Serializable(with = MutableStateSerializer::class)
    var language: MutableState<LanguageType>

    @Serializable
    var hideOnMinimize: MutableState<Boolean>

    constructor() {
        try {
            val prop: AppSettings = Json.decodeFromString(settingsFile.readText())

            this.threadCount = mutableStateOf(
                if (prop.threadCount.value > Runtime.getRuntime().availableProcessors())
                    max(
                        Runtime.getRuntime().availableProcessors(),
                        1
                    )
                else
                    prop.threadCount.value
            )

            this.theme = prop.theme
            this.propertiesList = prop.propertiesList
            this.language = prop.language
            this.hideOnMinimize = prop.hideOnMinimize
        } catch (e: Exception) {
            logger.error {
                "Failed to load app settings. Loading defaults."
            }
            this.threadCount = mutableStateOf(
                max(
                    Runtime.getRuntime().availableProcessors() /2,
                    1
                )
            )
            this.theme = mutableStateOf(ThemeType.Dark)
            this.propertiesList = mutableSetOf()
            this.language = mutableStateOf(LanguageType.Default)
            this.hideOnMinimize = mutableStateOf(true)
        }
    }

    fun save () {
        settingsFile.writeText(Json.encodeToString(this))
    }
}
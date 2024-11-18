package ru.packetdima.datascanner.common

import androidx.compose.runtime.mutableStateOf
import ru.packetdima.datascanner.searcher.properties.Properties
import ru.packetdima.datascanner.ui.UIProperties

object Settings {
    lateinit var searcher: Properties
    lateinit var ui: UIProperties
    var scanningTaskPath = mutableStateOf("")
    var focusRequested = mutableStateOf(false)
}


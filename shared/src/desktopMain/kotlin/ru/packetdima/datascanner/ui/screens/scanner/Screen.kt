package ru.packetdima.datascanner.ui.screens.scanner

import ru.packetdima.datascanner.misc.FilesCounter
import java.io.File

sealed class Screen {
    object SelectPathScreen : Screen()
    data class ScanningScreen(val directory: File) : Screen()
    data class ResultScreen(
        val filesCounter: FilesCounter,
        val timeSpent: Int
    ) : Screen()
}
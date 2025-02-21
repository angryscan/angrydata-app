package ru.packetdima.datascanner.screens

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.use
import ru.packetdima.datascanner.scan.common.FilesCounter
import ru.packetdima.datascanner.searcher.WriterTest
import ru.packetdima.datascanner.ui.screens.scanner.ResultScreen
import kotlin.test.Test


internal class ResultScreenKtTest {
    @Test
    fun resultScreen() {
        val w = WriterTest()
        val filesCounter = FilesCounter()
        w.write()
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                ResultScreen(filesCounter, 0, onExported = {}) {}
            }
        }
    }
}
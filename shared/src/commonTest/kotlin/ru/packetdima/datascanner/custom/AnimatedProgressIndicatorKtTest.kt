package ru.packetdima.datascanner.custom

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.use
import ru.packetdima.datascanner.ui.AnimatedProgressIndicator
import kotlin.test.Test

internal class AnimatedProgressIndicatorKtTest {

    @Test
    fun animatedProgressIndicator() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                AnimatedProgressIndicator(
                    indicatorProgress = 0f,
                    color = Color.White,
                    backgroundColor = Color.Black
                )
                AnimatedProgressIndicator(
                    indicatorProgress = 0.5f,
                    color = Color.White,
                    backgroundColor = Color.Black
                )
                AnimatedProgressIndicator(
                    indicatorProgress = 1.0f,
                    color = Color.White,
                    backgroundColor = Color.Black
                )
            }
        }
    }
}
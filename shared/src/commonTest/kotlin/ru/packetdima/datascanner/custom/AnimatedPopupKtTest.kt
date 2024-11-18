package ru.packetdima.datascanner.custom

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import ru.packetdima.datascanner.ui.custom.AnimatedPopup
import kotlin.test.Test

internal class AnimatedPopupKtTest {

    @Test
    fun animatedPopup() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                AnimatedPopup(
                    density = Density(1f),
                    expanded = true,
                    onClose = {},
                    content = {}
                )
            }
        }
    }
}
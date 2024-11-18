package ru.packetdima.datascanner.custom

import androidx.compose.ui.test.*
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.ui.custom.ConfirmationDialog
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ConfirmationDialogKtTest : InitSettingsTest {
    init {
        initSettings()
    }
    @Test
    fun `Test confirmation dialog`() = runComposeUiTest {
        val text = "Test text"
        setContent {
            ConfirmationDialog(text, "", {}, {})
        }
        onNodeWithText(text).assertExists()
        onNodeWithTag("confirm_button").performClick()
        onNodeWithTag("decline_button").performClick()
    }
}
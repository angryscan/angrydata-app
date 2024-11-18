package ru.packetdima.datascanner.custom

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import org.junit.Test
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.ui.custom.ApplicationErrorWindow

@OptIn(ExperimentalTestApi::class)
class ApplicationErrorWindowKtTest : InitSettingsTest {
    init {
        initSettings()
    }
    @Test
    fun `Test application error window`() = runComposeUiTest{
        val text = "Error message"
        val exception = Exception(text)

        setContent {
            ApplicationErrorWindow(exception)
        }

        onNodeWithText(text).assertExists()
        onNodeWithTag("application_error_save").assertExists()
        onNodeWithTag("application_error_close").assertExists()
    }
}
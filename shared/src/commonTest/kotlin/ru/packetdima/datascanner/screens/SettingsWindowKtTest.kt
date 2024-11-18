package ru.packetdima.datascanner.screens
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import ru.packetdima.datascanner.common.DetectFunction
import ru.packetdima.datascanner.common.InitSettingsTest
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.searcher.FileType
import ru.packetdima.datascanner.ui.UIProperties
import ru.packetdima.datascanner.ui.windows.settings.SettingsWindow
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsWindowKtTest : InitSettingsTest {
    init {
        initSettings()
    }

    private fun ComposeUiTest.launchContent() {
        setContent {
            SettingsWindow(
                onCloseClick = {}
            )
        }
    }

    @Test
    fun `Test fast scan checkbox`() = runComposeUiTest {
        launchContent()

        onNodeWithTag("fastScanCheckbox").apply {
            // on
            performClick()
            assertIsOn()
            // off
            performClick()
            assertIsOff()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Test thread count slider`() = runComposeUiTest {
        launchContent()
        val maxCores = Runtime.getRuntime().availableProcessors()
        val coresCount = Integer.min(Settings.searcher.threadCount.value, maxCores)
        onNodeWithTag("thread_count_text").assertTextEquals(coresCount.toString())

        onNodeWithTag("thread_count_slider").performMouseInput {
            click(Offset(1f, 0f))
        }
        onNodeWithTag("thread_count_text").assertTextEquals("1")

        onNodeWithTag("thread_count_slider").performMouseInput {
            click(Offset(240.dp.toPx() - 1f, 0f))
        }
        onNodeWithTag("thread_count_text").assertTextEquals(maxCores.toString())
    }



    @Test
    fun `Test language button`() = runComposeUiTest{
        launchContent()

        onNodeWithTag("language_text", useUnmergedTree = true).assertTextEquals(UIProperties.LanguageType.Default.text)

        UIProperties.LanguageType.entries.forEach {
            onNodeWithTag("language_button").performClick()
            onNodeWithText(it.text).assertExists()
            onNodeWithText(it.text).performClick()
            onNodeWithTag("language_text", useUnmergedTree = true).assertTextEquals(it.text)
        }
    }

    @Test
    fun `Test theme button`() = runComposeUiTest{
        launchContent()

        UIProperties.ThemeType.entries.forEach {
            onNodeWithTag("theme_button").performClick()
            onAllNodesWithText(it.name).onLast().performClick() // их может быть 1 или 2
            onNodeWithTag("theme_text", useUnmergedTree = true).assertTextEquals(it.name)
        }
    }

    @Test
    fun `Test extensions to scan checkbox`() = runComposeUiTest{
        launchContent()

        FileType.entries.map {it.name}.forEach {
            onNodeWithTag("checkbox_$it").run {
                assertExists()
                performScrollTo() // элемент должен быть видимым
                performClick()
            }
            if (it in listOf("ZIP")) // выключены по умолчанию
                onNodeWithTag("checkbox_$it").assertIsOn()
            else
                onNodeWithTag("checkbox_$it").assertIsOff()
        }
    }
    @Test
    fun `Test detect functions checkbox`() = runComposeUiTest{
        launchContent()

        // снимем три флажка
        val exclude = listOf(DetectFunction.CVV, DetectFunction.CarNumber, DetectFunction.Login)
        exclude.forEach {
            onNodeWithTag("checkbox_$it").run {
                assertExists()
                assertIsOn()
                performScrollTo() // элемент должен быть видимым
                performClick()
                assertIsOff()
            }
        }

        // проверим, что флажки сняты
        DetectFunction.entries.toTypedArray().forEach {
            onNodeWithTag("checkbox_$it").assertExists()
            onNodeWithTag("checkbox_$it").performScrollTo()

            if (it in exclude)
                onNodeWithTag("checkbox_$it").assertIsOff()
            else
                onNodeWithTag("checkbox_$it").assertIsOn()
        }
    }
}
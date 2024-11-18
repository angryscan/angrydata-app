package ru.packetdima.datascanner
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.use
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import ru.packetdima.datascanner.main
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.ui.UIProperties
import ru.packetdima.datascanner.ui.custom.ApplicationErrorWindow
import ru.packetdima.datascanner.ui.theme.CustomTheme
import ru.packetdima.datascanner.ui.windows.MainWindow
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class MainKtTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun customTheme() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                CustomTheme { }
            }

        }
    }

    @Test
    fun guiRunTest() {
        rule.runOnUiThread {
            val uiPath = javaClass.getResource("/common/ui.json")
            assertNotNull(uiPath)
            Settings.ui = UIProperties(uiPath.file)

            Settings.ui.theme.value = UIProperties.ThemeType.Dark
            var isVisible = true

            ImageComposeScene(width = 1024, height = 768).use { window ->
                window.setContent {
                    MainWindow(isVisible) { isVisible = it}
                }
            }
            Settings.ui.theme.value = UIProperties.ThemeType.Light
            ImageComposeScene(width = 1024, height = 768).use { window ->
                window.setContent {
                    MainWindow(isVisible) { isVisible = it}
                }
            }
            Settings.ui.theme.value = UIProperties.ThemeType.System
            ImageComposeScene(width = 1024, height = 768).use { window ->
                window.setContent {
                    MainWindow(isVisible) { isVisible = it}
                }
            }
        }
    }

    @Test
    fun onError() {
        val uiPath = javaClass.getResource("/common/ui.json")
        assertNotNull(uiPath)
        Settings.ui = UIProperties(uiPath.file)
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                ApplicationErrorWindow(Exception("SomeError"))
            }
        }
    }

    @Test
    fun mainTest() = runBlocking {
        main(arrayOf("-console"))
//        composeTestRule.runOnUiThread {
//            ImageComposeScene(width = 1024, height = 768).use { window ->
//                window.setContent {
//                    runBlocking {
//                        main(arrayOf())
//                    }
//                }
//            }
//        }
    }
}
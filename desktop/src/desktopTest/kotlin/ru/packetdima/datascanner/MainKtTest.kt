package ru.packetdima.datascanner

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.use
import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.db.DatabaseSettings
import ru.packetdima.datascanner.di.scanModule
import ru.packetdima.datascanner.di.settingsModule
import ru.packetdima.datascanner.ui.MainWindow
import ru.packetdima.datascanner.ui.theme.AppTheme
import ru.packetdima.datascanner.ui.windows.ApplicationErrorWindow
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class MainKtTest : KoinTest {
    @get:Rule
    val rule = createComposeRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            module {
                single {
                    DatabaseSettings(
                        url = "jdbc:sqlite:build/tmp/test.db",
                        driver = "org.sqlite.JDBC"
                    )
                }

            },
            settingsModule,
            scanModule
        )
    }

    @Test
    fun customTheme() {
        val appSettings = get<AppSettings>()
        appSettings.theme.value = AppSettings.ThemeType.System
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                AppTheme { }
            }
        }
        appSettings.theme.value = AppSettings.ThemeType.Dark
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                AppTheme { }
            }
        }
        appSettings.theme.value = AppSettings.ThemeType.Light
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                AppTheme { }
            }
        }
    }

    @Test
    fun guiRunTest() {
        rule.runOnUiThread {
            val uiPath = javaClass.getResource("/common/ui.json")?.file
            assertNotNull(uiPath)
            var isVisible = true

            val appSettings = get<AppSettings>()

            appSettings.theme.value = AppSettings.ThemeType.System
            ImageComposeScene(width = 1024, height = 768).use { window ->
                window.setContent {
                    MainWindow(
                        isVisible = isVisible,
                        onHideRequest = { isVisible = false },
                        onCloseRequest = {

                        }
                    )
                }
            }
            appSettings.theme.value = AppSettings.ThemeType.Dark
            ImageComposeScene(width = 1024, height = 768).use { window ->
                window.setContent {
                    MainWindow(
                        isVisible = isVisible,
                        onHideRequest = { isVisible = false },
                        onCloseRequest = {

                        }
                    )
                }
            }
            appSettings.theme.value = AppSettings.ThemeType.Light
            ImageComposeScene(width = 1024, height = 768).use { window ->
                window.setContent {
                    MainWindow(
                        isVisible = isVisible,
                        onHideRequest = { isVisible = false },
                        onCloseRequest = {

                        }
                    )
                }
            }
        }
    }

    @Test
    fun onError() {
        ImageComposeScene(width = 1024, height = 768).use { window ->
            window.setContent {
                ApplicationErrorWindow(Exception("SomeError"))
            }
        }
    }
}
package ru.packetdima.datascanner.searcher

import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTestRule
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.common.UserSignatureSettings
import ru.packetdima.datascanner.db.DatabaseSettings
import ru.packetdima.datascanner.di.scanModule
import kotlin.test.Test

internal class SearcherResultTest {
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
            module {
                single { javaClass.getResource("common/UserSignatures.json")
                    ?.let { it1 -> UserSignatureSettings.SettingsFile(it1.path) } }
                single { UserSignatureSettings() }
                single { javaClass.getResource("common/AppSettings.json")
                    ?.let { it1 -> AppSettings.AppSettingsFile(it1.path) } }
                single { AppSettings() }
                single { javaClass.getResource("common/ScanSettings.json")
                    ?.let { it1 -> ScanSettings.SettingsFile(it1.path) } }
                single { ScanSettings() }
            },
            scanModule
        )
    }

    @Test
    fun test() {
        val w = WriterTest()
        w.write()
        for (col in SearcherResult.columnNames)
            continue
        for (row in SearcherResult.getPreview(0))
            for (cell in row)
                continue
        //res.saveResult()
    }
}
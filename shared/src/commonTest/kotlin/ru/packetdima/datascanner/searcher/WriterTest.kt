package ru.packetdima.datascanner.searcher

import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTestRule
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.common.UserSignatureSettings
import ru.packetdima.datascanner.db.DatabaseSettings
import ru.packetdima.datascanner.di.scanModule
import ru.packetdima.datascanner.scan.common.Document
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WriterTest {
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
    fun initTest() {
        runBlocking {
            Writer.initDB()
        }

        SearcherResult.initResultTable()

        val doc = Document(1024, "123")
        doc + mapOf(DetectFunction.Name to 1)
        doc + mapOf(DetectFunction.Emails to 1)

        runBlocking {
            Writer.write(listOf(doc))
        }
        SearcherResult.createResult()

        assertEquals("123", SearcherResult.getPreview(0)[0][0])
    }


    @Test
    fun write() {
        runBlocking {
            Writer.initDB()
        }

        SearcherResult.initResultTable()

        val doc = Document(1024, "123")
        doc + mapOf(DetectFunction.Name to 1)
        doc + mapOf(DetectFunction.Emails to 1)

        runBlocking {
            Writer.write(listOf(doc))
        }

        SearcherResult.createResult()

        assertEquals("123", SearcherResult.getPreview(0)[0][0])
    }
}
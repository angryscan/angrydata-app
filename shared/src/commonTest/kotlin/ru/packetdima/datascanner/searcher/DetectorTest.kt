package ru.packetdima.datascanner.searcher

import info.downdetector.bigdatascanner.common.Cleaner
import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.coroutines.Dispatchers
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
import ru.packetdima.datascanner.scan.common.FileType
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class DetectorTest {
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
    fun scan() {
        val sampleText = """Sample text, 4279432112344321 199-510-399 13  
583410778676
омс 7755320882002755"""
        val doc = Document(1, "123")
        DetectFunction.entries.map { f ->
            f to f.scan(Cleaner.cleanText(sampleText)).takeIf { it > 0 }
        }.mapNotNull { p ->
            p.second?.let { p.first to it }
        }.toMap().forEach { (t, u) ->
            doc.updateDocument(t, u)
        }
        assertEquals(4, doc.length())
    }

    @Test
    fun testText() {
        val file = javaClass.getResource("/files/TestText.txt")?.file
        assertNotNull(file)
        for (attribute in DetectFunction.entries) {
            assertEquals(1, getCountOfAttribute(file, attribute))
        }
    }

    @Test
    fun testCardEdge() {
        val file = javaClass.getResource("/files/cardNumber/edge.txt")?.file
        assertNotNull(file)
        assertEquals(5, getCountOfAttribute(file, DetectFunction.CardNumbers))
    }

    @Test
    fun testCardWithBrace() {
        val file = javaClass.getResource("/files/cardNumber/braces.txt")?.file
        assertNotNull(file)
        assertEquals(3, getCountOfAttribute(file, DetectFunction.CardNumbers))
    }

    @Test
    fun testCardWithSmth() {
        val file = javaClass.getResource("/files/cardNumber/smth.txt")?.file
        assertNotNull(file)
        assertEquals(4, getCountOfAttribute(file, DetectFunction.CardNumbers))
    }

    @Test
    fun testCardWithStar() {
        val file = javaClass.getResource("/files/cardNumber/star.txt")?.file
        assertNotNull(file)
        assertEquals(2, getCountOfAttribute(file, DetectFunction.CardNumbers))
    }

    @Test
    fun testCardNotValid() {
        val file = javaClass.getResource("/files/cardNumber/notValid.txt")?.file
        assertNotNull(file)
        assertEquals(0, getCountOfAttribute(file, DetectFunction.CardNumbers))
    }

    @Test
    fun testSnilsEdge() {
        val file = javaClass.getResource("/files/snils/edge.txt")?.file
        assertNotNull(file)
        assertEquals(6, getCountOfAttribute(file, DetectFunction.SNILS))
    }

    @Test
    fun testSnilsWithBrace() {
        val file = javaClass.getResource("/files/snils/braces.txt")?.file
        assertNotNull(file)
        assertEquals(5, getCountOfAttribute(file, DetectFunction.SNILS))
    }

    @Test
    fun testSnilsWithSmth() {
        val file = javaClass.getResource("/files/snils/smth.txt")?.file
        assertNotNull(file)
        assertEquals(6, getCountOfAttribute(file, DetectFunction.SNILS))
    }

    @Test
    fun testSnilsWithStar() {
        val file = javaClass.getResource("/files/snils/star.txt")?.file
        assertNotNull(file)
        assertEquals(1, getCountOfAttribute(file, DetectFunction.SNILS))
    }

    @Test
    fun testSnilsNotValid() {
        val file = javaClass.getResource("/files/snils/notValid.txt")?.file
        assertNotNull(file)
        assertEquals(0, getCountOfAttribute(file, DetectFunction.SNILS))
    }

    @Test
    fun testInnEdge() {
        val file = javaClass.getResource("/files/inns/edge.txt")?.file
        assertNotNull(file)
        assertEquals(6, getCountOfAttribute(file, DetectFunction.INN))
    }

    @Test
    fun testInnWithBrace() {
        val file = javaClass.getResource("/files/inns/braces.txt")?.file
        assertNotNull(file)
        assertEquals(3, getCountOfAttribute(file, DetectFunction.INN))
    }

    @Test
    fun testInnWithSmth() {
        val file = javaClass.getResource("/files/inns/smth.txt")?.file
        assertNotNull(file)
        assertEquals(6, getCountOfAttribute(file, DetectFunction.INN))
    }

    @Test
    fun testInnWithStar() {
        val file = javaClass.getResource("/files/inns/star.txt")?.file
        assertNotNull(file)
        assertEquals(1, getCountOfAttribute(file, DetectFunction.INN))
    }

    @Test
    fun testInnNotValid() {
        val file = javaClass.getResource("/files/inns/notValid.txt")?.file
        assertNotNull(file)
        assertEquals(0, getCountOfAttribute(file, DetectFunction.INN))
    }

    @Test
    fun testPassports() {
        val file = javaClass.getResource("/files/passport/passport.txt")?.file
        assertNotNull(file)
        assertEquals(1, getCountOfAttribute(file, DetectFunction.Passport))
    }

    private fun getCountOfAttribute(filePath: String, detectFunction: DetectFunction): Int {

        val file = File(filePath)

        assertEquals(true, file.exists())

        val coroutineContext = Dispatchers.Default

        val document = runBlocking(coroutineContext) {
            FileType.getFileType(file)?.scanFile(file, coroutineContext, listOf(detectFunction), false).let {
                assertNotNull(it)
            }
        }
        return document.getDocumentFields().getOrDefault(detectFunction, 0)
    }
}

package ru.packetdima.datascanner.searcher

import org.apache.poi.openxml4j.util.ZipSecureFile
import info.downdetector.bigdatascanner.common.Cleaner
import info.downdetector.bigdatascanner.common.DetectFunction
import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.*
import ru.packetdima.datascanner.scan.common.FileType
import java.io.File
import java.io.FileWriter
import kotlin.test.*

internal class FileTypeTest() {
    init {
        ZipSecureFile.setMinInflateRatio(-1.0) // отключение срабатывания исключения для zip-бомбы
    }

    @Test
    fun `Check file types`() {
        listOf(
            "1.docx",
            "emails_result.xlsx",
            "third.xlsx",
            "veryLong/very_long.xlsx",
            "veryLong/very_long.docx",
            "TestText.txt",
            "5.csv",
            "small.xls",
            "first.doc",
            "first.xls",
            "first.docx",
            "first.xlsx",
            "first.odt",
            "first.odp",
            "first.otp",
            "first.pptx",
            "first.potx",
            "first.ppsx",
            "first.pptm",
            "first.ppt",
            "first.pps",
            "first.pot",
            "first.ods",
            "first.pdf",
            "first.zip",
            "very_short.xlsx",
            "TestText.rar",
            "ipv6.txt"
        )
            .forEach { filename ->
                runBlocking {
                    try {
                        print("Scanning file: $filename")
                        val millis = System.currentTimeMillis()
                        val path = javaClass.getResource("/files/$filename")
                        assertNotNull(path)
                        val f = File(path.file)
                        val enumType: FileType? = f.let { FileType.getFileType(it) }
                        enumType?.scanFile(f, currentCoroutineContext(), DetectFunction.entries, false).let { doc ->
                            Matrix.getMap(filename)
                                ?.let { m -> assertEquals(m, doc?.getDocumentFields(), "File: $filename") }
                                ?: println("Нет данных для $filename")
                        }
                        println("; OK; time: ${System.currentTimeMillis() - millis}")
                    } catch (e: Exception) {
                        fail(e.message)
                    }
                }
            }
    }

    // проверить на очень длинном файле
    @Test
    fun `Check fast and full scan`() {
        val filelist = listOf(
            "very_long.log",
            "very_long.xlsx",
            "very_long.docx",
            "very_long.txt",
            "very_long.csv",
            "very_long.xml",
            "very_long.json",
            "very_long.doc",
            "very_long.xls",
            "very_long.pdf"
        )
            .map { filename -> "veryLong/$filename" }

        fun checkScan(filename: String, map: Map<IDetectFunction, Int>?, isFastScan: Boolean = false) {

            val path = javaClass.getResource("/files/$filename")
            assertNotNull(path)
            val f = File(path.file)
            val enumType: FileType? = f.let { FileType.getFileType(it) }

            runBlocking {
                enumType?.scanFile(f, currentCoroutineContext(), DetectFunction.entries, isFastScan).let {
                    assertNotNull(it)
                    assertEquals(map, it.getDocumentFields())
                }
            }
        }

        val f = javaClass.getResource("/files/veryLong/very_long.log")?.file
        val text = f?.let { File(it).readText() }?.let { Cleaner.cleanText(it) }
        if (text != null) {
            println(text.slice(0..100))
        }
        println(text?.let { DetectFunction.IPv6.scan(it) })

        println("Checking fast scan")
        filelist.forEach { filename ->
            println(filename)
            val map = Matrix.getMap(filename, true)
            checkScan(filename, map, true)
        }
        println("Checking full scan")
        filelist.forEach { filename ->
            println(filename)
            val map = Matrix.getMap(filename, false)
            checkScan(filename, map, false)
        }
    }

    @Test
    fun `Check FileNotFoundException`() {
        val f = File("notExist.txt")
        assertFalse(f.exists())
        runBlocking {
            try {
                val enumType: FileType? = FileType.getFileType(f)
                enumType?.scanFile(f, currentCoroutineContext(), DetectFunction.entries, false).let {
                    assertEquals(mapOf(), it?.getDocumentFields())
                    assertTrue(it?.skipped() == true)
                }
            } catch (e: Exception) {
                fail(e.message)
            }
        }
    }

    @Test
    fun `Check IllegalArgumentException`() {
        val path = javaClass.getResource("/files/diacritic.zip")
        assertNotNull(path)
        val f = File(path.file)
        val writer = FileWriter(f)
        writer.write("content")
        writer.close()
        assertTrue(f.exists())

        runBlocking {
            try {
                FileType.ZIP.scanFile(f, currentCoroutineContext(), DetectFunction.entries, false).let {
                    assertEquals(0, it.length())
                    assertEquals(mapOf(), it.getDocumentFields())
                }
            } catch (e: Exception) {
                fail(e.message)
            }
        }
    }

    @Test
    fun `Check empty doc file exception`() {
        val path = javaClass.getResource("/files/empty.doc")
        assertNotNull(path)
        val f = File(path.file)

        val writer = FileWriter(f)
        writer.write("content")
        writer.close()
        assertTrue(f.exists())

        runBlocking {
            try {
                FileType.DOC.scanFile(f, currentCoroutineContext(), DetectFunction.entries, false).let {
                    assertEquals(0, it.length())
                    assertEquals(mapOf(), it.getDocumentFields())
                }
            } catch (e: Exception) {
                fail(e.message)
            }
        }
    }
}
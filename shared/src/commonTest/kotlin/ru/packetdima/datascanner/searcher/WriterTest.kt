package ru.packetdima.datascanner.searcher

import info.downdetector.bigdatascanner.common.DetectFunction
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.scan.common.Document
import kotlin.test.Test
import kotlin.test.assertEquals

internal class WriterTest {

    @Test
    fun initTest() {
        if (AppFiles.ResultDBFile.exists())
            if (!AppFiles.ResultDBFile.delete())
                throw Exception("Cannot delete db file")
        Database.connect(
            "jdbc:sqlite:${AppFiles.ResultDBFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )

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
        if (AppFiles.ResultDBFile.exists())
            if (!AppFiles.ResultDBFile.delete())
                throw Exception("Cannot delete db file")
        Database.connect(
            "jdbc:sqlite:${AppFiles.ResultDBFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )

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
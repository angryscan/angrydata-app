package ru.packetdima.datascanner.searcher

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.packetdima.datascanner.common.AppFiles
import info.downdetector.bigdatascanner.common.IDetectFunction
import ru.packetdima.datascanner.common.Settings
import ru.packetdima.datascanner.scan.common.FileSize
import ru.packetdima.datascanner.searcher.model.Attributes
import ru.packetdima.datascanner.searcher.model.Reports
import ru.packetdima.datascanner.searcher.model.ScannedFiles
import ru.packetdima.datascanner.searcher.properties.Properties
import ru.packetdima.datascanner.ui.UIProperties
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class SensitiveSearcherTest {
    @Test
    fun `Check whole dir scan`() {
        val path = javaClass.getResource("/files")?.file
        assertNotNull(path)

        inspectDirectory(path)

        val results = mutableMapOf<String, MutableMap<IDetectFunction, Int>>()
        transaction {
            Reports.join(Attributes, JoinType.INNER, Reports.attribute, Attributes.id)
                .join(ScannedFiles, JoinType.INNER, Reports.file, ScannedFiles.id)
                .selectAll()
                .forEach {
                    val filename = it[ScannedFiles.path].replace("\\", "/").substringAfterLast("/files/")
                    val count = it[Reports.count]
                    Matrix.getFnByName(it[Attributes.name]) ?. let { funname ->
                        results[filename]?.set(funname, count)
                    }
                }
        }
        results.forEach { (filename, arr) ->
            if (Matrix.contains(filename))
                assertEquals(Matrix.getMap(filename), arr)
        }

    }

    @Test
    fun `Check very many files scan`() {
        val folder = createTempDirectory("ADS").toFile()
        folder.deleteOnExit()

        (1 until 300).forEach {
            val filename = File.createTempFile("ADS", ".txt", folder)
            filename.writeText("+79251234567")
            filename.deleteOnExit()
        }

        inspectDirectory(folder.absolutePath)

        transaction {
            Reports.selectAll()
                .forEach {
                    assertEquals(1, it[Reports.count])
                    assertEquals(2, it[Reports.attribute].value) // "phones"
                }
        }
    }
    fun inspectDirectory(path : String) = runBlocking {
        val uiPath = javaClass.getResource("/common/ui.json")
        val searcherPath = javaClass.getResource("/common/properties.json")
        assertNotNull(uiPath)
        assertNotNull(searcherPath)
        Settings.ui = UIProperties(uiPath.file)
        Settings.searcher = Properties(searcherPath.file)
        val onProgressChange: (Pair<Long, FileSize>) -> Unit = {}
        val onFileFound: (Long) -> Unit = {}
        val onSkipSelectFile: (Long) -> Unit = {}
        val onSkipScanFile: (Long) -> Unit = {}
        val onReportCreated: (filesCount: Pair<Long, FileSize>) -> Unit = {}
        val searcher = SensitiveSearcher()

        Database.connect(
            "jdbc:sqlite:${AppFiles.ResultDBFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )

        Writer.initDB()

        searcher.inspectDirectory(
            File(path),
            onProgressChange,
            onFileFound,
            onSkipSelectFile,
            onSkipScanFile,
            onReportCreated
        )
    }
}
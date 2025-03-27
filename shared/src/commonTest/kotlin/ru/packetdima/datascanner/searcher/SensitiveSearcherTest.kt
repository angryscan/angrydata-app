package ru.packetdima.datascanner.searcher

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.junit.Rule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import org.koin.test.KoinTestRule
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.common.UserSignatureSettings
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.db.DatabaseSettings
import ru.packetdima.datascanner.di.scanModule
import ru.packetdima.datascanner.scan.common.FileSize
import ru.packetdima.datascanner.searcher.model.Attributes
import ru.packetdima.datascanner.searcher.model.Reports
import ru.packetdima.datascanner.searcher.model.ScannedFiles
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class SensitiveSearcherTest: KoinComponent {
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

    val database by inject<DatabaseConnector>()


    @Test
    fun `Check whole dir scan`() {
        val path = javaClass.getResource("/files")?.file
        assertNotNull(path)

        inspectDirectory(path)

        val results = mutableMapOf<String, MutableMap<IDetectFunction, Int>>()
        runBlocking {
            database.transaction {
                Reports.join(Attributes, JoinType.INNER, Reports.attribute, Attributes.id)
                    .join(ScannedFiles, JoinType.INNER, Reports.file, ScannedFiles.id)
                    .selectAll()
                    .forEach {
                        val filename = it[ScannedFiles.path].replace("\\", "/").substringAfterLast("/files/")
                        val count = it[Reports.count]
                        Matrix.getFnByName(it[Attributes.name])?.let { funname ->
                            results[filename]?.set(funname, count)
                        }
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

        runBlocking {
            database.transaction {
                Reports.selectAll()
                    .forEach {
                        assertEquals(1, it[Reports.count])
                        assertEquals(2, it[Reports.attribute].value) // "phones"
                    }
            }
        }
    }
    fun inspectDirectory(path : String) = runBlocking {
        val onProgressChange: (Pair<Long, FileSize>) -> Unit = {}
        val onFileFound: (Long) -> Unit = {}
        val onSkipSelectFile: (Long) -> Unit = {}
        val onSkipScanFile: (Long) -> Unit = {}
        val onReportCreated: (filesCount: Pair<Long, FileSize>) -> Unit = {}
        val searcher = SensitiveSearcher()

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
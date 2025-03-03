package ru.packetdima.datascanner

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.koin.core.context.startKoin
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.AppVersion
import ru.packetdima.datascanner.common.OS
import ru.packetdima.datascanner.di.databaseModule
import ru.packetdima.datascanner.di.scanModule
import ru.packetdima.datascanner.di.settingsModule
import ru.packetdima.datascanner.scan.common.ScanPathHelper
import ru.packetdima.datascanner.ui.MainWindow
import ru.packetdima.datascanner.ui.tray.DorkTray
import ru.packetdima.datascanner.ui.windows.ApplicationErrorWindow
import java.awt.event.WindowEvent
import java.io.File
import java.net.BindException
import java.sql.Connection
import java.util.*
import javax.swing.JOptionPane
import javax.swing.UIManager
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main(args: Array<String>) {
    File(System.getProperty("java.io.tmpdir")).listFiles()?.toList()?.asFlow()?.map {
        if (it.name.startsWith("ADS_")) {
            it.delete()
        }
    }?.collect()


    System.setProperty(
        "skiko.renderApi",
        when (OS.currentOS()) {
            OS.WINDOWS -> "OPENGL"
            OS.LINUX -> "OPENGL"
            OS.MAC -> "METAL"
            else -> "OPENGL"
        }
    )

    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (_: Exception) {
        logger.warn { "Cannot load system look and feel" }
    }

    val port = 10201
    val selectorManager = SelectorManager(Dispatchers.IO)

    try {
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", port)
        logger.info { "Server started at port $port" }
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val socket = serverSocket.accept()
                    logger.debug { "Client connected" }
                    val input = socket.openReadChannel()
                    val path = input.readUTF8Line()
                    socket.close()
                    if (path != null && File(path).exists()) {
                        ScanPathHelper.setPath(path)
                        logger.debug { "Path received: $path" }
                        ScanPathHelper.requestFocus()
                    }
                } catch (e: Exception) {
                    logger.error { "Error when receiving data from client: ${e.message}" }
                }
            }
        }
    } catch (e: BindException) {
        logger.info { "Application already running!" }
        if (args.isNotEmpty() && args.first().let { it != "-c" && it != "-console" }) {
            runBlocking {
                val path = args.firstOrNull()
                if (path != null && File(path).exists()) {
                    logger.info { "Sending path to running app" }
                    val clientSocket = aSocket(selectorManager).tcp().connect("127.0.0.1", port)
                    val output = clientSocket.openWriteChannel(autoFlush = true)
                    output.writeFully(path.toByteArray())
                    clientSocket.close()
                    selectorManager.close()
                    logger.info { "Path sent to running app" }
                }
            }
        }
        logger.info { "Terminating app" }
        exitProcess(0)
    } catch (e: Exception) {
        logger.error { e.message }
        exitProcess(1)
    }

    var lastError: Throwable? by mutableStateOf(null)

    val password =
        if (AppVersion == "Debug")
            ""
        else
            UUID.randomUUID().toString()

    if (AppFiles.ResultDBFile.exists()) {
        if (!AppFiles.ResultDBFile.delete()) {
            JOptionPane.showMessageDialog(
                null,
                "Cannot access to database. Check it is in use by another process!",
                "Error!",
                JOptionPane.ERROR_MESSAGE
            )
            logger.error { "Cannot access to database. Check it is in use by another process!" }
            return
        }
    }

    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (_: Exception) {
        logger.warn { "Cannot load system look and feel" }
    }

    startKoin {
        modules(
            settingsModule,
            databaseModule,
            scanModule
        )
    }

    Database.connect(
        "jdbc:sqlite:${AppFiles.ResultDBFile.absolutePath}",
        driver = "org.sqlite.JDBC",
        password = password
    )
    TransactionManager.manager.defaultIsolationLevel =
        Connection.TRANSACTION_SERIALIZABLE


    if (args.isNotEmpty() &&
        arrayOf("-c", "-console", "-h", "-help").any { args.contains(it) }
    ) {
        if (arrayOf("-c", "-console").any { args.contains(it) })
            Console.consoleRun(args)
        else if (arrayOf("-h", "-help").any { args.contains(it) }) {
            Console.help()
        }
    } else {
        if (args.isNotEmpty()) {
            logger.warn { "Started with ${args.size} argument(s):" }
            args.forEach {
                logger.warn { "Argument: $it" }
            }
            ScanPathHelper.setPath(args.first())
        }
        application(exitProcessOnExit = false) {
            CompositionLocalProvider(
                LocalWindowExceptionHandlerFactory provides WindowExceptionHandlerFactory { window ->
                    WindowExceptionHandler {
                        lastError = it
                        window.dispatchEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                        throw it
                    }
                }
            ) {
                var mainIsVisible by remember { mutableStateOf(true) }

                MainWindow(
                    onCloseRequest = ::exitApplication,
                    onHideRequest = {
                        mainIsVisible = false
                    },
                    isVisible = mainIsVisible
                )
                DorkTray(
                    mainIsVisible = mainIsVisible,
                    mainVisibilitySet = {
                        mainIsVisible = it
                    }
                )
            }
        }
        if (lastError != null) {
            singleWindowApplication(
                state = WindowState(width = 600.dp, height = 500.dp),
                exitProcessOnExit = false
            ) {
                ApplicationErrorWindow(lastError!!)
            }
            exitProcess(1)
        } else {
            exitProcess(0)
        }
    }
}
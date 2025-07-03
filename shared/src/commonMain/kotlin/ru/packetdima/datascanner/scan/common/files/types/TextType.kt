package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.mozilla.universalchardet.UniversalDetector
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.Location
import ru.packetdima.datascanner.scan.common.files.LocationFinder.ScanException
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import kotlin.coroutines.CoroutineContext

object TextType : IFileType {
    override suspend fun scanFile(
        file: File,
        context: CoroutineContext,
        detectFunctions: List<IDetectFunction>,
        fastScan: Boolean
    ): Document {
        val str = StringBuilder()
        val res = Document(file.length(), file.absolutePath)
        val buf = CharArray(1000)
        var sample = 0
        try {
            withContext(Dispatchers.IO) {
                val encoding = UniversalDetector.detectCharset(file)
                FileInputStream(file).use { fileInputStream ->
                    fileInputStream.bufferedReader(charset = Charset.forName(encoding)).use { reader ->
                        var actualRead: Int
                        while (true) {
                            actualRead = reader.read(buf)
                            if (actualRead <= 0) {
                                break
                            }

                            str.append(buf)

                            if (isLengthOverload(str.length, isActive)) {
                                res + withContext(context) { scan(str.toString(), detectFunctions) }
                                str.clear()
                                sample++
                                if (isSampleOverload(sample, fastScan, isActive))
                                    return@withContext
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            res.skip()
            return res
        }
        if (str.isNotEmpty() && !isSampleOverload(sample, fastScan)) {
            res + withContext(context) { scan(str.toString(), detectFunctions) }
        }
        return res
    }

    override suspend fun findLocation(
        filePath: String,
        detectFunction: IDetectFunction,
        fastScan: Boolean
    ): List<Location> {
        var length = 0
        var sample = 0
        var lineNumber = 1
        val locations = mutableListOf<Location>()
        try {
            withContext(Dispatchers.IO) {
                val file = File(filePath)
                val encoding = UniversalDetector.detectCharset(file)
                FileInputStream(file).use { fileInputStream ->
                    file.bufferedReader(charset = Charset.forName(encoding)).use { reader ->
                        var line = reader.readLine()
                        while (line != null) {
                            getEntries(line, detectFunction)
                                .forEach {
                                    locations.add(Location(it, "N $lineNumber"))
                                }

                            length += line.length
                            lineNumber++
                            if (isLengthOverload(length, isActive)) {
                                length = 0
                                sample++
                                if (isSampleOverload(sample, fastScan, isActive))
                                    return@withContext
                            }
                            line = reader.readLine()
                        }
                    }
                }
            }
        }
        catch (_: Exception) {
            throw ScanException
        }
        return locations
    }
}
package ru.packetdima.datascanner.scan.common.files

import info.downdetector.bigdatascanner.common.IDetectFunction
import ru.packetdima.datascanner.scan.common.files.types.XLSXType
import java.io.File

object LocationFinder {
    fun isSupported(type: FileType): Boolean = when (type) {
        FileType.XLSX -> true
        else -> false
    }

    suspend fun findLocations(filePath: String, detectFunction: IDetectFunction): List<Location> {
        val file = File(filePath)
        val type = FileType.getFileType(file = file)
        if (type == null || !isSupported(type))
            throw NotSupportedTypeException

        return when (type) {
            FileType.XLSX -> XLSXType.findLocation(filePath, detectFunction)
            else -> throw NotSupportedTypeException
        }
    }

    val NotSupportedTypeException = Exception("Not supported file type")
    val ScanException = Exception("Scan error")
}
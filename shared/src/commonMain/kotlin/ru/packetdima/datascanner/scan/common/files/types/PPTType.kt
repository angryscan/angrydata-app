package ru.packetdima.datascanner.scan.common.files.types

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.apache.poi.hslf.usermodel.HSLFSlideShow
import org.apache.poi.hslf.usermodel.HSLFTable
import org.apache.poi.hslf.usermodel.HSLFTextBox
import ru.packetdima.datascanner.scan.common.Document
import ru.packetdima.datascanner.scan.common.files.Location
import ru.packetdima.datascanner.scan.common.files.LocationFinder.ScanException
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext

object PPTType : IFileType {
    override suspend fun scanFile(
        file: File,
        context: CoroutineContext,
        detectFunctions: List<IDetectFunction>,
        fastScan: Boolean
    ): Document {
        val str = StringBuilder()
        val res = Document(file.length(), file.absolutePath)
        var sample = 0
        try {
            withContext(Dispatchers.IO) {
                FileInputStream(file).use { fileInputStream ->
                    HSLFSlideShow(fileInputStream).use { presentation ->
                        presentation.slides.forEach { slide ->
                            str.append(slide.slideName).append("\n")
                            str.append(slide.title).append("\n")

                            slide.shapes.forEach { shape ->
                                when (shape) {
                                    is HSLFTextBox -> {
                                        str.append(shape.text).append("\n")
                                        if (isLengthOverload(str.length, isActive)) {
                                            res + withContext(context) { scan(str.toString(), detectFunctions) }
                                            str.clear()
                                            sample++
                                            if (isSampleOverload(sample, fastScan, isActive))
                                                return@withContext
                                        }
                                    }

                                    is HSLFTable -> {
                                        for (row in 0..shape.numberOfRows - 1) {
                                            for (col in 0..shape.numberOfColumns - 1) {
                                                str.append(shape.getCell(row, col).text).append("\n")
                                                if (isLengthOverload(str.length, isActive)) {
                                                    res + withContext(context) {
                                                        scan(
                                                            str.toString(),
                                                            detectFunctions
                                                        )
                                                    }
                                                    str.clear()
                                                    sample++
                                                    if (isSampleOverload(sample, fastScan, isActive))
                                                        return@withContext
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            slide.comments.forEach { comment ->
                                str.append(comment.text).append("\n")
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
        val locations = mutableListOf<Location>()
        try {
            withContext(Dispatchers.IO) {
                val file = File(filePath)
                FileInputStream(file).use { fileInputStream ->
                    HSLFSlideShow(fileInputStream).use { presentation ->
                        presentation.slides.forEachIndexed { slideIndex, slide ->

                            if (slide.slideName != null) {
                                getEntries(slide.slideName, detectFunction)
                                    .forEach {
                                        locations.add(
                                            Location(
                                                it,
                                                "Slide: ${slideIndex + 1}"
                                            )
                                        )
                                    }
                                length += slide.slideName.length
                            }

                            if (slide.title != null) {
                                getEntries(slide.title, detectFunction)
                                    .forEach {
                                        locations.add(
                                            Location(
                                                it,
                                                "Slide: ${slideIndex + 1}"
                                            )
                                        )
                                    }
                                length += slide.title.length
                            }

                            slide.shapes.forEach { shape ->
                                when (shape) {
                                    is HSLFTextBox -> {
                                        getEntries(shape.text, detectFunction)
                                            .forEach {
                                                locations.add(
                                                    Location(
                                                        it,
                                                        "Slide: ${slideIndex + 1}"
                                                    )
                                                )
                                            }
                                        length += shape.text.length

                                        if (isLengthOverload(length, isActive)) {
                                            length = 0
                                            sample++
                                            if (isSampleOverload(sample, fastScan, isActive)) return@withContext
                                        }
                                    }

                                    is HSLFTable -> {
                                        for (row in 0..shape.numberOfRows - 1) {
                                            for (col in 0..shape.numberOfColumns - 1) {
                                                getEntries(shape.getCell(row, col).text, detectFunction)
                                                    .forEach {
                                                        locations.add(
                                                            Location(
                                                                it,
                                                                "Slide: ${slideIndex + 1}"
                                                            )
                                                        )
                                                    }
                                                if (isLengthOverload(length, isActive)) {
                                                    length = 0
                                                    sample++
                                                    if (isSampleOverload(sample, fastScan, isActive)) return@withContext
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            slide.comments.forEach { comment ->
                                getEntries(comment.text, detectFunction)
                                    .forEach {
                                        locations.add(
                                            Location(
                                                it,
                                                "Slide: ${slideIndex + 1}"
                                            )
                                        )
                                    }
                                if (isLengthOverload(length, isActive)) {
                                    length = 0
                                    sample++
                                    if (isSampleOverload(sample, fastScan, isActive)) return@withContext
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            throw ScanException
        }
        return locations
    }
}
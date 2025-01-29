package ru.packetdima.datascanner.searcher

import com.github.junrar.Archive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.HWPFOldDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.dhatim.fastexcel.reader.ReadableWorkbook
import org.mozilla.universalchardet.UniversalDetector
import info.downdetector.bigdatascanner.common.Cleaner
import info.downdetector.bigdatascanner.common.IDetectFunction
import org.odftoolkit.simple.PresentationDocument
import org.odftoolkit.simple.TextDocument
import ru.packetdima.datascanner.common.Settings
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.coroutines.CoroutineContext


enum class FileType(val extensions: List<String>) {
    XLSX(listOf("xlsx")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            var sample = 0
            try {
                withContext(Dispatchers.IO) {
                    FileInputStream(file).use { inputStream ->
                        ReadableWorkbook(inputStream).use { workbook ->
                            workbook.sheets.use { sheets ->
                                sheets.forEach { sheet ->
                                    sheet?.openStream().use { rowStream ->
                                        rowStream?.forEach rowStream@{ row ->
                                            row?.forEach { cell ->
                                                if (cell != null) {
                                                    str.append(cell.text).append("\n")
                                                    if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                                        res + scan(str.toString())
                                                        str.clear()
                                                        sample++
                                                        if (isSampleOverload(sample) || !isActive) return@rowStream
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                res.skip()
                return res
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }
            return res
        }
    },
    DOCX(listOf("docx")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            var sample = 0
            try {
                withContext(Dispatchers.IO) {
                    FileInputStream(file).use { fileInputStream ->
                        XWPFDocument(fileInputStream).use stream@{ document ->
                            document.bodyElements
                            for (elem in document.bodyElements) {
                                when (elem) {
                                    is XWPFParagraph -> elem.text
                                    is XWPFTable -> elem.text
                                    else -> ""
                                }.forEach { c ->
                                    str.append(c)
                                    if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                        res + withContext(context) { scan(str.toString()) }
                                        str.clear()
                                        sample++
                                        if (isSampleOverload(sample) || !isActive) return@stream
                                    }
                                }
                                str.append("\n")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                try {
                    withContext(Dispatchers.IO) {
                        FileInputStream(file).use { fileInputStream ->
                            HWPFDocument(fileInputStream).use { document ->
                                WordExtractor(document).use { extractor ->
                                    extractor.text.forEach { c ->
                                        str.append(c).append("\n")
                                        if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                            res + withContext(context) { scan(str.toString()) }
                                            str.clear()
                                            sample++
                                            if (isSampleOverload(sample) || !isActive) return@forEach
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    res.skip()
                    return res
                }
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }
            return res
        }
    },
    Text((1..999).map { it.toString().padStart(3, '0') } + listOf("txt", "csv", "xml", "json", "log")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            val cbuf = CharArray(1000)
            var sample = 0
            try {
                val encoding = UniversalDetector.detectCharset(file)
                withContext(Dispatchers.IO) {
                    FileInputStream(file).use { fileInputStream ->
                        fileInputStream.bufferedReader(charset = Charset.forName(encoding)).use { reader ->
                            var actualRead: Int
                            while (true) {
                                actualRead = reader.read(cbuf)
                                if (actualRead <= 0) {
                                    break
                                }

                                str.append(cbuf)

                                if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                    res + withContext(context) { scan(str.toString()) }
                                    str.clear()
                                    sample++
                                    if (isSampleOverload(sample) || !isActive) break
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                res.skip()
                return res
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }
            return res
        }
    },
    DOC(listOf("doc")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            var sample = 0
            try {
                withContext(Dispatchers.IO) {
                    FileInputStream(file).use { inputStream ->
                        WordExtractor(inputStream).use { wordExtractor ->
                            wordExtractor.text.forEach { c ->
                                str.append(c)
                                if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                    res + withContext(context) { scan(str.toString()) }
                                    str.clear()
                                    sample++
                                    if (isSampleOverload(sample) || !isActive) return@forEach
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                try {
                    withContext(Dispatchers.IO) {
                        POIFSFileSystem(file).use { inputStream ->
                            HWPFOldDocument(inputStream).use { hwpfOldDocument ->
                                hwpfOldDocument.documentText.forEach { c ->
                                    str.append(c)
                                    if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                        res + withContext(context) { scan(str.toString()) }
                                        str.clear()
                                        sample++
                                        if (isSampleOverload(sample) || !isActive) return@forEach
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    res.skip()
                    return res
                }
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }
            return res
        }
    },
    XLS(listOf("xls")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            var sample = 0
            try {
                //Create Workbook instance holding reference to .xlsx file
                withContext(Dispatchers.IO) {
                    FileInputStream(file).use { fileInputStream ->
                        HSSFWorkbook(fileInputStream).use { workbook ->
                            workbook.forEach workbook@{ sheet ->
                                sheet?.forEach { row ->
                                    row?.forEach { cell ->
                                        if (cell != null) {
                                            when (cell.cellType) {
                                                CellType.NUMERIC -> str.append(cell.numericCellValue).append("\n")
                                                CellType.STRING -> str.append(cell.stringCellValue).append("\n")
                                                else -> {}
                                            }
                                            if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                                res + withContext(context) { scan(str.toString()) }
                                                str.clear()
                                                sample++
                                                if (isSampleOverload(sample) || !isActive) return@workbook
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                res.skip()
                return res
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }
            return res
        }
    },
    PDF(listOf("pdf")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            var sample = 0
            try {
                withContext(Dispatchers.IO) {
                    PDDocument.load(file).use { document ->
                        PDFTextStripper().getText(document).forEach { c ->
                            str.append(c)
                            if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                res + withContext(context) { scan(str.toString()) }
                                str.clear()
                                sample++
                                if (isSampleOverload(sample) || !isActive) return@forEach
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                res.skip()
                return res
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }
            return res
        }
    },
    ODT(listOf("odt")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            var sample = 0


            try {
                withContext(Dispatchers.IO) {
                    TextDocument.loadDocument(file).use { document ->
                        val parIterator = document.paragraphIterator
                        while (parIterator.hasNext()) {
                            val paragraph = parIterator.next()

                            str.append(paragraph.textContent)
                            if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                res + withContext(context) { scan(str.toString()) }
                                str.clear()
                                sample++
                                if (isSampleOverload(sample) || !isActive) return@withContext
                            }
                        }
                        str.append("\n")

                        val tableIterator = document.tableList.iterator()
                        while (tableIterator.hasNext()) {
                            val table = tableIterator.next()

                            table.rowList.forEach { r ->
                                for (i in 0..r.cellCount - 1) {
                                    str.append(r.getCellByIndex(i).displayText).append("\n")
                                    if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                        res + withContext(context) { scan(str.toString()) }
                                        str.clear()
                                        sample++
                                        if (isSampleOverload(sample) || !isActive) return@withContext
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                res.skip()
                return res
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }

            return res
        }
    },
    ODP(listOf("odp")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val str = StringBuilder()
            val res = Document(file.length(), file.absolutePath)
            var sample = 0


            try {
                withContext(Dispatchers.IO) {
                    PresentationDocument.loadDocument(file).use { document ->
                        val slideIterator = document.slides
                        while (slideIterator.hasNext()) {
                            val slide = slideIterator.next()
                            str.append(slide.slideName).append("\n")

                            slide.tableList.forEach { table ->
                                table.rowList.forEach { r ->
                                    for (i in 0..r.cellCount - 1) {
                                        str.append(r.getCellByIndex(i).displayText).append("\n")
                                        if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                            res + withContext(context) { scan(str.toString()) }
                                            str.clear()
                                            sample++
                                            if (isSampleOverload(sample) || !isActive) return@withContext
                                        }
                                    }
                                }
                            }

                            val listIterator = slide.listIterator
                            while(listIterator.hasNext()) {
                                val list = listIterator.next()
                                str.append(list.header)
                                list.items.forEach {
                                    str.append(it.textContent).append("\n")
                                    if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                        res + withContext(context) { scan(str.toString()) }
                                        str.clear()
                                        sample++
                                        if (isSampleOverload(sample) || !isActive) return@withContext
                                    }
                                }
                            }
                            val textboxIterator = slide.textboxIterator
                            while(textboxIterator.hasNext()) {
                                val textbox = textboxIterator.next()
                                str.append(textbox.textContent).append("\n")
                                if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                    res + withContext(context) { scan(str.toString()) }
                                    str.clear()
                                    sample++
                                    if (isSampleOverload(sample) || !isActive) return@withContext
                                }
                            }
                            val noteListIterator = slide.notesPage?.listIterator
                            if(noteListIterator != null) {
                                while(noteListIterator.hasNext()) {
                                    val noteList = noteListIterator.next()
                                    noteList.items.forEach {
                                        str.append(it.textContent).append("\n")
                                        if (str.length >= Settings.searcher.sampleLength || !isActive) {
                                            res + withContext(context) { scan(str.toString()) }
                                            str.clear()
                                            sample++
                                            if (isSampleOverload(sample) || !isActive) return@withContext
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                res.skip()
                return res
            }
            if (str.isNotEmpty() && !isSampleOverload(sample)) {
                res + withContext(context) { scan(str.toString()) }
            }

            return res
        }
    },
    ZIP(listOf("zip")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val res = Document(file.length(), file.absolutePath)
            var skipped = 0
            var all = 0
            var reading = true
            try {
                withContext(Dispatchers.IO) {
                    FileInputStream(file).use { fileInputStream ->
                        BufferedInputStream(fileInputStream).use { bufferedInputStream ->
                            ZipInputStream(
                                bufferedInputStream,
                                Charset.forName("windows-1251")
                            ).use { zipInputStream ->
                                var zipEntry: ZipEntry?
                                val buffer = ByteArray(2048)
                                while (reading) {
                                    zipEntry = try {
                                        zipInputStream.nextEntry.also {
                                            if (it == null)
                                                reading = false
                                        }
                                    } catch (e: NullPointerException) {
                                        null
                                    } catch (e: IllegalArgumentException) {
                                        null
                                    }
                                    if (zipEntry == null) continue

                                    // не распаковывать если расширение не из выбранных
                                    if (!selectedExtension(zipEntry.name))
                                        continue

                                    val tmpFile = File.createTempFile(
                                        "ADS_",
                                        "." + zipEntry.name.substringAfterLast(".")
                                    )
                                    try {
                                        tmpFile.outputStream().use { fileOutputStream ->
                                            fileOutputStream.buffered(buffer.size).use { bufferedOutputStream ->
                                                while (true) {
                                                    val length = zipInputStream.read(buffer)
                                                    if (length <= 0) break
                                                    bufferedOutputStream.write(buffer, 0, length)
                                                }
                                                bufferedOutputStream.flush()
                                            }
                                        }
                                        getFileType(tmpFile)?.scanFile(tmpFile, context)?.also { doc ->
                                            if (!doc.skipped()) {
                                                res + doc.getDocumentFields()
                                            } else {
                                                skipped++
                                            }
                                        }
                                        all++
                                    } catch (e: IOException) {
                                        continue
                                    } finally {
                                        tmpFile.delete()
                                    }
                                    zipInputStream.closeEntry()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (res.isEmpty()) {
                    res.skip()
                    return res
                }
            }
            if (skipped == all)
                res.skip()
            return res
        }
    },
    RAR(listOf("rar")) {
        override suspend fun scanFile(file: File, context: CoroutineContext): Document {
            val res = Document(file.length(), file.absolutePath)
            var skipped = 0
            var all = 0
            try {
                withContext(Dispatchers.IO) {
                    val archive = Archive(file)
                    while (true) {
                        val fileHeader = archive.nextFileHeader() ?: break

                        if (!selectedExtension(fileHeader.fileName))
                            continue

                        val tmpFile = File.createTempFile(
                            "ADS_",
                            "." + fileHeader.fileName.substringAfterLast(".")
                        )

                        try {
                            archive.extractFile(fileHeader, tmpFile.outputStream())
                            getFileType(tmpFile)?.scanFile(tmpFile, context)?.also { doc ->
                                if (!doc.skipped()) {
                                    res + doc.getDocumentFields()
                                } else {
                                    skipped++
                                }
                            }
                            all++
                        } catch (e: IOException) {
                            continue
                        } finally {
                            tmpFile.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                if (res.isEmpty()) {
                    res.skip()
                    return res
                }
            }
            if (skipped == all)
                res.skip()
            return res
        }
    };

    abstract suspend fun scanFile(file: File, context: CoroutineContext): Document

    protected fun scan(text: String): Map<IDetectFunction, Int> {
        val cleanText = Cleaner.cleanText(text)
        return Settings.searcher.detectFunctions.map { f ->
            f to f.scan(cleanText).takeIf { it > 0 }
        }.mapNotNull { p ->
            p.second?.let { p.first to it }
        }.toMap() + Settings.searcher.userSignature.map { f ->
            f to f.scan(cleanText).takeIf { it > 0 }
        }.mapNotNull { p ->
            p.second?.let { p.first to it }
        }.toMap()
    }

    companion object {
        fun getFileType(file: File): FileType? {
            return entries.find { fileType -> fileType.extensions.contains(file.extension) }
        }

        private fun selectedExtension(fileName: String): Boolean =
            FileType.entries.filter {
                Settings.searcher.extensions.contains(it.name)
            }.flatMap {
                it.extensions
            }.any { fileName.endsWith(it) }

        private fun isSampleOverload(sample: Int): Boolean {
            return (Settings.searcher.fastScan.value && sample >= Settings.searcher.sampleCount)
        }
    }
}
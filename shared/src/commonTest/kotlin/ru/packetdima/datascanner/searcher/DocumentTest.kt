package ru.packetdima.datascanner.searcher

import ru.packetdima.datascanner.common.DetectFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class DocumentTest {

    @Test
    fun updateDocument() {
        val document = Document(1, "123")
        document.updateDocument(DetectFunction.Name, 0)
        document.updateDocument(DetectFunction.Emails, 1)
        assertEquals(1, document.length())
        assertEquals(false, document.isEmpty())
        assertFailsWith<Exception> {
            document.updateDocument(DetectFunction.valueOf("Not existing field"), 1)
        }
    }

    @Test
    fun getSensitivity() {
        val document = Document(1, "123")
        document + mapOf(DetectFunction.Name to 0)
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals(1, document.funDetected())
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals(1, document.funDetected())
        document + mapOf(DetectFunction.Name to 1)
        assertEquals(2, document.funDetected())
    }

    @Test
    fun isEmpty() {
        val document = Document(1, "123")
        document + mapOf(DetectFunction.Name to 0)
        assertEquals(true, document.isEmpty())
        document + mapOf(DetectFunction.Name to 1)
        assertEquals(false, document.isEmpty())
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals(false, document.isEmpty())
    }

    @Test
    fun getLength() {
        val document = Document(1, "123")
        document + mapOf(DetectFunction.Name to 0)
        assertEquals(0, document.length())
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals(1, document.length())
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals(1, document.length())
        document + mapOf(DetectFunction.Name to 1)
        assertEquals(2, document.length())
    }

    @Test
    fun testToString() {
        val document = Document(1, "123")
        document + mapOf(DetectFunction.Name to 0)
        assertEquals("{}", document.toString())
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals("{Emails=1}", document.toString())
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals("{Emails=2}", document.toString())
        document + mapOf(DetectFunction.Name to 1)
        assertEquals("{Emails=2, Name=1}", document.toString())
    }

    @Test
    fun getDocumentFields() {
        val expected = mapOf(DetectFunction.Emails to 1)
        val document = Document(1, "123")
        document + mapOf(DetectFunction.Name to 0)
        assertEquals(mapOf(), document.getDocumentFields())
        document + mapOf(DetectFunction.Emails to 1)
        assertEquals(expected, document.getDocumentFields())
    }

    @Test
    fun getSize() {
        val document = Document(1, "123")
        assertEquals(1, document.size)
    }

    @Test
    fun getDocumentID() {
        val document = Document(1, "123")
        assertEquals("123", document.path)
    }
}
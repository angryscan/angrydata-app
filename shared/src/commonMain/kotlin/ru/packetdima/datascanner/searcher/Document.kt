@file:Suppress("BooleanMethodIsAlwaysInverted")

package ru.packetdima.datascanner.searcher

import ru.packetdima.datascanner.common.DetectFunction

class Document(val size: Long, val path: String) {
/* This is main struct in this library - searcher.Document. All texts represent as searcher.Document finally */

    private var skipped = false

    private var documentFields: MutableMap< DetectFunction, Int> = mutableMapOf()

    fun skip(): Document {
        skipped = true
        return this
    }

    fun skipped() = skipped

    // update document value
    fun updateDocument(field: DetectFunction, value: Int) {
        if(value > 0)
            documentFields[field] = (documentFields[field] ?: 0) + value
    }

    // getValue document funDetected
    fun funDetected(): Int = documentFields.size

    // is document empty
    fun isEmpty(): Boolean = documentFields.isEmpty()

    fun length(): Int = this.documentFields.size

    // getValue document
    fun getDocumentFields(): Map<DetectFunction, Int> {
        return documentFields.toMap()
    }

    operator fun plus(other: Map<DetectFunction, Int>): Document {
        other.forEach { (f, v) ->
            updateDocument(f, v)
        }
        return this
    }

    override fun toString(): String {
        return this.getDocumentFields().toString()
    }
}
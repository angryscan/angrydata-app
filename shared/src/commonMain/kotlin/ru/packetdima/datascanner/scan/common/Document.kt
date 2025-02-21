@file:Suppress("BooleanMethodIsAlwaysInverted")

package ru.packetdima.datascanner.scan.common

import info.downdetector.bigdatascanner.common.IDetectFunction

class Document(val size: Long, val path: String) {
/* This is main struct in this library - searcher.Document. All texts represent as searcher.Document finally */

    private var skipped = false

    private var documentFields: MutableMap<IDetectFunction, Int> = mutableMapOf()

    fun skip(): Document {
        skipped = true
        return this
    }

    fun skipped() = skipped

    // update document value
    fun updateDocument(field: IDetectFunction, value: Int) {
        if(value > 0)
            documentFields[field] = (documentFields[field] ?: 0) + value
    }

    // getValue document funDetected
    fun funDetected(): Int = documentFields.size

    // is document empty
    fun isEmpty(): Boolean = documentFields.isEmpty()

    fun length(): Int = this.documentFields.size

    // getValue document
    fun getDocumentFields(): Map<IDetectFunction, Int> {
        return documentFields.toMap()
    }

    operator fun plus(other: Map<IDetectFunction, Int>): Document {
        other.forEach { (f, v) ->
            updateDocument(f, v)
        }
        return this
    }

    override fun toString(): String {
        return this.getDocumentFields().toString()
    }
}
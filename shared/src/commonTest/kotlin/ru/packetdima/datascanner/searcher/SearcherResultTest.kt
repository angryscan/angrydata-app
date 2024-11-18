package ru.packetdima.datascanner.searcher

import kotlin.test.Test

internal class SearcherResultTest {

    @Test
    fun test() {
        val w = WriterTest()
        w.write()
        for (col in SearcherResult.columnNames)
            continue
        for (row in SearcherResult.getPreview(0))
            for (cell in row)
                continue
        //res.saveResult()
    }
}
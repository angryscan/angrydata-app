package ru.packetdima.datascanner.types

import IKoinTestRule
import ru.packetdima.datascanner.scan.common.files.types.XLSXType
import kotlin.test.Test


class XLSXTypeTest : IKoinTestRule {
    @Test
    fun findLocation() {
        val fileName = "first/first.xls"

        CheckLocation.checkByMap(
            fileName,
            XLSXType::findLocation
        )
    }
}
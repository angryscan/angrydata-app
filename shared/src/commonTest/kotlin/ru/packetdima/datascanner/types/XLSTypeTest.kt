package ru.packetdima.datascanner.types

import IKoinTestRule
import ru.packetdima.datascanner.scan.common.files.types.XLSType
import kotlin.test.Test

class XLSTypeTest : IKoinTestRule {

    @Test
    fun findLocation() {
        val fileName = "first/first.xls"

        CheckLocation.checkByMap(
            fileName,
            XLSType::findLocation
        )
    }

}
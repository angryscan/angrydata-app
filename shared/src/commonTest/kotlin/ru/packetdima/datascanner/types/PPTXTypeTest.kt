package ru.packetdima.datascanner.types

import IKoinTestRule
import ru.packetdima.datascanner.scan.common.files.types.PPTXType
import kotlin.test.Test

class PPTXTypeTest : IKoinTestRule {
    @Test
    fun findLocation() {
        val fileName = "first/first.pptx"

        CheckLocation.checkByMap(
            fileName,
            PPTXType::findLocation
        )
    }
}
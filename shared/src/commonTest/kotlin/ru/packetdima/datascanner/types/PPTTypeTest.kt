package ru.packetdima.datascanner.types

import IKoinTestRule
import ru.packetdima.datascanner.scan.common.files.types.PPTType
import kotlin.test.Test

class PPTTypeTest : IKoinTestRule {
    @Test
    fun findLocation() {
        val fileName = "first/first.ppt"

        CheckLocation.checkByMap(
            fileName,
            PPTType::findLocation
        )
    }
}
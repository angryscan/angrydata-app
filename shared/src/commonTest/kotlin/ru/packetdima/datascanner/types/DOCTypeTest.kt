package ru.packetdima.datascanner.types

import IKoinTestRule
import ru.packetdima.datascanner.scan.common.files.types.DOCType
import kotlin.test.Test

class DOCTypeTest : IKoinTestRule {
    @Test
    fun findLocation() {
        val fileName = "first/first.doc"

        CheckLocation.checkByMap(
            fileName = fileName,
            DOCType::findLocation
        )
    }
}
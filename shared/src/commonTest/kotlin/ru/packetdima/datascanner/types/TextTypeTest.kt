package ru.packetdima.datascanner.types

import IKoinTestRule
import ru.packetdima.datascanner.scan.common.files.types.TextType
import kotlin.test.Test

class TextTypeTest : IKoinTestRule {
    @Test
    fun findLocation() {
        val fileName = "first/first.csv"

        CheckLocation.checkByMap(
            fileName,
            TextType::findLocation
        )
    }
}
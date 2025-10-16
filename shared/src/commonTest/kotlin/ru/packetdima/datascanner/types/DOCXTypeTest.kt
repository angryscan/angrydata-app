package ru.packetdima.datascanner.types

import IKoinTestRule
import ru.packetdima.datascanner.scan.common.files.types.DOCXType
import kotlin.test.Test

class DOCXTypeTest: IKoinTestRule {
    @Test
    fun findLocation() {
        val fileName = "first/first.docx"

        CheckLocation.checkByMap(
            fileName,
            DOCXType::findLocation
        )
    }
}
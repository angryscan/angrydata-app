package ru.packetdima.datascanner.scan.functions

import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.serialization.Serializable

@Serializable
object CertDetectFun : IDetectFunction {
    override val name: String = "CertDetectFunction"
    override val writeName: String = "CertDetectFunction"

    private val regex = """(---BEGIN CERTIFICATE)|(---BEGIN PKCS7)|(---BEGIN.*?KEY)"""
        .toRegex(RegexOption.MULTILINE)

    override fun scan(text: String): Sequence<String> {
        return regex
            .findAll(text)
            .map { it.value }
    }
}
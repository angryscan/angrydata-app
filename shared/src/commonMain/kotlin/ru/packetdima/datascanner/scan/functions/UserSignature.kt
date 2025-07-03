package ru.packetdima.datascanner.scan.functions

import androidx.compose.runtime.mutableStateListOf
import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.serialization.Serializable
import org.jetbrains.skia.Pattern

@Serializable
data class UserSignature(
    override var name: String,
    override var writeName: String,
    val searchSignatures: MutableList<String> = mutableStateListOf()
) : IDetectFunction {

    override fun scan(text: String): Sequence<String> {
        return searchSignatures
            .map { sig -> Pattern.quote(sig).toRegex(RegexOption.IGNORE_CASE).findAll(text).map { it.value } }
            .flatMap { it }
            .asSequence()
    }

    override fun toString(): String {
        return this.name
    }
}
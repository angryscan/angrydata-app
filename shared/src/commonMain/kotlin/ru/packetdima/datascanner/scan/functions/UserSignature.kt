package ru.packetdima.datascanner.scan.functions

import androidx.compose.runtime.mutableStateListOf
import info.downdetector.bigdatascanner.common.IDetectFunction
import info.downdetector.bigdatascanner.common.extensions.MatchWithContext
import kotlinx.serialization.Serializable
import org.jetbrains.skia.Pattern

@Serializable
data class UserSignature(
    override var name: String,
    override var writeName: String,
    val searchSignatures: MutableList<String> = mutableStateListOf()
) : IDetectFunction {

    override fun scan(text: String, withContext: Boolean): Sequence<MatchWithContext> {
        return searchSignatures
            .map { sig -> Pattern.quote(sig)
                .toRegex(RegexOption.IGNORE_CASE)
                .findAll(text)
                .map { match ->
                    MatchWithContext(
                        match.value,
                        before = text.substring(
                            maxOf(0,match.range.start - 10),
                            match.range.start
                        ),
                        after = ("$text ").substring(
                            match.range.last + 1,
                            minOf(match.range.last + 11, text.length)
                        )
                    )
                }
            }
            .flatMap { it }
            .asSequence()
    }

    override fun toString(): String {
        return this.name
    }
}
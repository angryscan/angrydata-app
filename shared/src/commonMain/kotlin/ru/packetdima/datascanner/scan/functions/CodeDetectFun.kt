package ru.packetdima.datascanner.scan.functions

import info.downdetector.bigdatascanner.common.IDetectFunction
import info.downdetector.bigdatascanner.common.extensions.MatchWithContext
import kotlinx.serialization.Serializable

@Serializable
object CodeDetectFun: IDetectFunction {
    override val name: String = "CodeDetectFunction"
    override val writeName: String = "CodeDetectFunction"

    override fun scan(text: String, withContext: Boolean): Sequence<MatchWithContext> = sequenceOf()

}
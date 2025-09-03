package ru.packetdima.datascanner.scan.functions

import info.downdetector.bigdatascanner.common.IDetectFunction
import info.downdetector.bigdatascanner.common.extensions.MatchWithContext
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.packetdima.datascanner.scan.functions.rkn.DomainRepository

@Serializable
object RKNDomainDetectFun: IDetectFunction, KoinComponent {
    override val name = "RKNDomainDetectFun"
    override val writeName = "RKNDomainDetectFun"

    private val regex = """(?<=https?\s)?([\w.-]+\.[a-z]{2,})(?=\s|$)"""
        .toRegex(RegexOption.MULTILINE)

    override fun scan(text: String, withContext: Boolean): Sequence<MatchWithContext> {
        val domainRepo by inject<DomainRepository>()
        return regex
            .findAll(text)
            .filter { domainRepo.checkDomain(it.value) }
            .map {MatchWithContext(it.value) }

    }
}
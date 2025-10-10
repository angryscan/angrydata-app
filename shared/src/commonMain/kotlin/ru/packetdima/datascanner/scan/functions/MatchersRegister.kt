package ru.packetdima.datascanner.scan.functions

import org.angryscan.common.engine.IMatcher
import org.angryscan.common.matchers.*

object MatchersRegister {
    val matchers = listOf<IMatcher>(
        AccountNumber,
        Address,
        CardNumber(),
        CarNumber,
        CVV,
        Email,
        FullName,
        INN,
        IP,
        IPv6,
        Login,
        OMS,
        Passport,
        Password,
        Phone,
        SNILS,
    )
}
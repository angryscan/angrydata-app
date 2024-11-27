package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import info.downdetector.bigdatascanner.common.DetectFunction
import ru.packetdima.datascanner.resources.*

@OptIn(ExperimentalResourceApi::class)
suspend fun DetectFunction.readableName(): String {
    return when (this) {
        DetectFunction.Name -> getString(Res.string.dfName)
        DetectFunction.Emails -> getString(Res.string.dfEmails)
        DetectFunction.Phones -> getString(Res.string.dfPhones)
        DetectFunction.CardNumbers -> getString(Res.string.dfCardNumbers)
        DetectFunction.CarNumber -> getString(Res.string.dfCarNumber)
        DetectFunction.SNILS -> getString(Res.string.dfSNILS)
        DetectFunction.Passport -> getString(Res.string.dfPassport)
        DetectFunction.OMS -> getString(Res.string.dfOMS)
        DetectFunction.INN -> getString(Res.string.dfINN)
        DetectFunction.AccountNumber -> getString(Res.string.dfAccountNumber)
        DetectFunction.Address -> getString(Res.string.dfAddress)
        DetectFunction.BlackList -> getString(Res.string.dfBlackList)
        DetectFunction.ValuableInfo -> getString(Res.string.dfValuableInfo)
        DetectFunction.Login -> getString(Res.string.dfLogin)
        DetectFunction.Password  -> getString(Res.string.dfPassword)
        DetectFunction.CVV -> getString(Res.string.dfCVV)
        DetectFunction.IP -> getString(Res.string.dfIP)
        DetectFunction.IPv6 -> getString(Res.string.dfIPv6)
        else -> this.writeName
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DetectFunction.composableName(): String {
    return when (this) {
        DetectFunction.Name -> stringResource(Res.string.dfName)
        DetectFunction.Emails -> stringResource(Res.string.dfEmails)
        DetectFunction.Phones -> stringResource(Res.string.dfPhones)
        DetectFunction.CardNumbers -> stringResource(Res.string.dfCardNumbers)
        DetectFunction.CarNumber -> stringResource(Res.string.dfCarNumber)
        DetectFunction.SNILS -> stringResource(Res.string.dfSNILS)
        DetectFunction.Passport -> stringResource(Res.string.dfPassport)
        DetectFunction.OMS -> stringResource(Res.string.dfOMS)
        DetectFunction.INN -> stringResource(Res.string.dfINN)
        DetectFunction.AccountNumber -> stringResource(Res.string.dfAccountNumber)
        DetectFunction.Address -> stringResource(Res.string.dfAddress)
        DetectFunction.BlackList -> stringResource(Res.string.dfBlackList)
        DetectFunction.ValuableInfo -> stringResource(Res.string.dfValuableInfo)
        DetectFunction.Login -> stringResource(Res.string.dfLogin)
        DetectFunction.Password  -> stringResource(Res.string.dfPassword)
        DetectFunction.CVV -> stringResource(Res.string.dfCVV)
        DetectFunction.IP -> stringResource(Res.string.dfIP)
        DetectFunction.IPv6 -> stringResource(Res.string.dfIPv6)
        else -> this.writeName
    }
}
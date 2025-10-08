package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import info.downdetector.bigdatascanner.common.DetectFunction
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.*

// Маппинг DetectFunction к ресурсам
private val detectFunctionResources = mapOf(
    DetectFunction.Name to Pair(Res.string.DetectFunction_Name, Res.string.DetectFunction_Description_Name),
    DetectFunction.Emails to Pair(Res.string.DetectFunction_Emails, Res.string.DetectFunction_Description_Emails),
    DetectFunction.Phones to Pair(Res.string.DetectFunction_Phones, Res.string.DetectFunction_Description_Phones),
    DetectFunction.CardNumbers to Pair(Res.string.DetectFunction_CardNumbers, Res.string.DetectFunction_Description_CardNumbers),
    DetectFunction.CarNumber to Pair(Res.string.DetectFunction_CarNumber, Res.string.DetectFunction_Description_CarNumber),
    DetectFunction.SNILS to Pair(Res.string.DetectFunction_SNILS, Res.string.DetectFunction_Description_SNILS),
    DetectFunction.Passport to Pair(Res.string.DetectFunction_Passport, Res.string.DetectFunction_Description_Passport),
    DetectFunction.OMS to Pair(Res.string.DetectFunction_OMS, Res.string.DetectFunction_Description_OMS),
    DetectFunction.INN to Pair(Res.string.DetectFunction_INN, Res.string.DetectFunction_Description_INN),
    DetectFunction.AccountNumber to Pair(Res.string.DetectFunction_AccountNumber, Res.string.DetectFunction_Description_AccountNumber),
    DetectFunction.Address to Pair(Res.string.DetectFunction_Address, Res.string.DetectFunction_Description_Address),
    DetectFunction.ValuableInfo to Pair(Res.string.DetectFunction_ValuableInfo, Res.string.DetectFunction_Description_ValuableInfo),
    DetectFunction.Login to Pair(Res.string.DetectFunction_Login, Res.string.DetectFunction_Description_Login),
    DetectFunction.Password to Pair(Res.string.DetectFunction_Password, Res.string.DetectFunction_Description_Password),
    DetectFunction.CVV to Pair(Res.string.DetectFunction_CVV, Res.string.DetectFunction_Description_CVV),
    DetectFunction.IP to Pair(Res.string.DetectFunction_IP, Res.string.DetectFunction_Description_IP),
    DetectFunction.IPv6 to Pair(Res.string.DetectFunction_IPv6, Res.string.DetectFunction_Description_IPv6),
    DetectFunction.Birthday to Pair(Res.string.DetectFunction_Birthday, Res.string.DetectFunction_Description_Birthday),
    DetectFunction.DeathDate to Pair(Res.string.DetectFunction_DeathDate, Res.string.DetectFunction_Description_DeathDate),
    DetectFunction.BirthCert to Pair(Res.string.DetectFunction_BirthCert, Res.string.DetectFunction_Description_BirthCert)
)

suspend fun DetectFunction.readableName(): String {
    return detectFunctionResources[this]?.first?.let { getString(it) }
        ?: throw IllegalStateException("No name resource for $this")
}

@Composable
fun DetectFunction.composableName(): String {
    return detectFunctionResources[this]?.first?.let { stringResource(it) }
        ?: throw IllegalStateException("No name resource for $this")
}

@Composable
fun DetectFunction.description(): String {
    return detectFunctionResources[this]?.second?.let { stringResource(it) }
        ?: throw IllegalStateException("No description resource for $this")
}
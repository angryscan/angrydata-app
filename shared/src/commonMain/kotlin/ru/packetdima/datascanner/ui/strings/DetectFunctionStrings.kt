package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import info.downdetector.bigdatascanner.common.DetectFunction
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.*

suspend fun DetectFunction.readableName(): String {
    return when (this) {
        DetectFunction.Name -> getString(Res.string.DetectFunction_Name)
        DetectFunction.Emails -> getString(Res.string.DetectFunction_Emails)
        DetectFunction.Phones -> getString(Res.string.DetectFunction_Phones)
        DetectFunction.CardNumbers -> getString(Res.string.DetectFunction_CardNumbers)
        DetectFunction.CarNumber -> getString(Res.string.DetectFunction_CarNumber)
        DetectFunction.SNILS -> getString(Res.string.DetectFunction_SNILS)
        DetectFunction.Passport -> getString(Res.string.DetectFunction_Passport)
        DetectFunction.OMS -> getString(Res.string.DetectFunction_OMS)
        DetectFunction.INN -> getString(Res.string.DetectFunction_INN)
        DetectFunction.AccountNumber -> getString(Res.string.DetectFunction_AccountNumber)
        DetectFunction.Address -> getString(Res.string.DetectFunction_Address)
        DetectFunction.ValuableInfo -> getString(Res.string.DetectFunction_ValuableInfo)
        DetectFunction.Login -> getString(Res.string.DetectFunction_Login)
        DetectFunction.Password -> getString(Res.string.DetectFunction_Password)
        DetectFunction.CVV -> getString(Res.string.DetectFunction_CVV)
        DetectFunction.IP -> getString(Res.string.DetectFunction_IP)
        DetectFunction.IPv6 -> getString(Res.string.DetectFunction_IPv6)
        else -> this.writeName
    }
}

@Composable
fun DetectFunction.composableName(): String {
    return when (this) {
        DetectFunction.Name -> stringResource(Res.string.DetectFunction_Name)
        DetectFunction.Emails -> stringResource(Res.string.DetectFunction_Emails)
        DetectFunction.Phones -> stringResource(Res.string.DetectFunction_Phones)
        DetectFunction.CardNumbers -> stringResource(Res.string.DetectFunction_CardNumbers)
        DetectFunction.CarNumber -> stringResource(Res.string.DetectFunction_CarNumber)
        DetectFunction.SNILS -> stringResource(Res.string.DetectFunction_SNILS)
        DetectFunction.Passport -> stringResource(Res.string.DetectFunction_Passport)
        DetectFunction.OMS -> stringResource(Res.string.DetectFunction_OMS)
        DetectFunction.INN -> stringResource(Res.string.DetectFunction_INN)
        DetectFunction.AccountNumber -> stringResource(Res.string.DetectFunction_AccountNumber)
        DetectFunction.Address -> stringResource(Res.string.DetectFunction_Address)
        DetectFunction.ValuableInfo -> stringResource(Res.string.DetectFunction_ValuableInfo)
        DetectFunction.Login -> stringResource(Res.string.DetectFunction_Login)
        DetectFunction.Password -> stringResource(Res.string.DetectFunction_Password)
        DetectFunction.CVV -> stringResource(Res.string.DetectFunction_CVV)
        DetectFunction.IP -> stringResource(Res.string.DetectFunction_IP)
        DetectFunction.IPv6 -> stringResource(Res.string.DetectFunction_IPv6)
        else -> this.writeName
    }
}

@Composable
fun DetectFunction.description(): String {
    return when (this) {
        DetectFunction.Name -> stringResource(Res.string.DetectFunction_Description_Name)
        DetectFunction.Emails -> stringResource(Res.string.DetectFunction_Description_Emails)
        DetectFunction.Phones -> stringResource(Res.string.DetectFunction_Description_Phones)
        DetectFunction.CardNumbers -> stringResource(Res.string.DetectFunction_Description_CardNumbers)
        DetectFunction.CarNumber -> stringResource(Res.string.DetectFunction_Description_CarNumber)
        DetectFunction.SNILS -> stringResource(Res.string.DetectFunction_Description_SNILS)
        DetectFunction.Passport -> stringResource(Res.string.DetectFunction_Description_Passport)
        DetectFunction.OMS -> stringResource(Res.string.DetectFunction_Description_OMS)
        DetectFunction.INN -> stringResource(Res.string.DetectFunction_Description_INN)
        DetectFunction.AccountNumber -> stringResource(Res.string.DetectFunction_Description_AccountNumber)
        DetectFunction.Address -> stringResource(Res.string.DetectFunction_Description_Address)
        DetectFunction.ValuableInfo -> stringResource(Res.string.DetectFunction_Description_ValuableInfo)
        DetectFunction.Login -> stringResource(Res.string.DetectFunction_Description_Login)
        DetectFunction.Password -> stringResource(Res.string.DetectFunction_Description_Password)
        DetectFunction.CVV -> stringResource(Res.string.DetectFunction_Description_CVV)
        DetectFunction.IP -> stringResource(Res.string.DetectFunction_Description_IP)
        DetectFunction.IPv6 -> stringResource(Res.string.DetectFunction_Description_IPv6)
    }
}
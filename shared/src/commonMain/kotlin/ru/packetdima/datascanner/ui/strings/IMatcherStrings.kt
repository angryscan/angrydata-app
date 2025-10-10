package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import org.angryscan.common.engine.IMatcher
import org.angryscan.common.matchers.*
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun
import ru.packetdima.datascanner.scan.functions.RKNDomainDetectFun
import org.angryscan.common.matchers.UserSignature
import ru.packetdima.datascanner.resources.*

@Composable
fun IMatcher.composableName(): String {
    return when (this) {
        is FullName -> stringResource(Res.string.Matcher_Name)
        is Email -> stringResource(Res.string.Matcher_Emails)
        is Phone -> stringResource(Res.string.Matcher_Phones)
        is CardNumber -> stringResource(Res.string.Matcher_CardNumbers)
        is CarNumber -> stringResource(Res.string.Matcher_CarNumber)
        is SNILS -> stringResource(Res.string.Matcher_SNILS)
        is Passport -> stringResource(Res.string.Matcher_Passport)
        is OMS -> stringResource(Res.string.Matcher_OMS)
        is INN -> stringResource(Res.string.Matcher_INN)
        is AccountNumber -> stringResource(Res.string.Matcher_AccountNumber)
        is Address -> stringResource(Res.string.Matcher_Address)
        is ValuableInfo -> stringResource(Res.string.Matcher_ValuableInfo)
        is Login -> stringResource(Res.string.Matcher_Login)
        is Password -> stringResource(Res.string.Matcher_Password)
        is CVV -> stringResource(Res.string.Matcher_CVV)
        is IP -> stringResource(Res.string.Matcher_IP)
        is IPv6 -> stringResource(Res.string.Matcher_IPv6)
        is CodeDetectFun -> stringResource(Res.string.Matcher_Code)
        is CertDetectFun -> stringResource(Res.string.Matcher_Cert)
        is RKNDomainDetectFun -> stringResource(Res.string.Matcher_DetectBlockedDomains)
        else -> this.name
    }
}

@Composable
fun IMatcher.description(): String {
    return when (this) {
        is FullName -> stringResource(Res.string.Matcher_Description_Name)
        is Email -> stringResource(Res.string.Matcher_Description_Emails)
        is Phone -> stringResource(Res.string.Matcher_Description_Phones)
        is CardNumber -> stringResource(Res.string.Matcher_Description_CardNumbers)
        is CarNumber -> stringResource(Res.string.Matcher_Description_CarNumber)
        is SNILS -> stringResource(Res.string.Matcher_Description_SNILS)
        is Passport -> stringResource(Res.string.Matcher_Description_Passport)
        is OMS -> stringResource(Res.string.Matcher_Description_OMS)
        is INN -> stringResource(Res.string.Matcher_Description_INN)
        is AccountNumber -> stringResource(Res.string.Matcher_Description_AccountNumber)
        is Address -> stringResource(Res.string.Matcher_Description_Address)
        is ValuableInfo -> stringResource(Res.string.Matcher_Description_ValuableInfo)
        is Login -> stringResource(Res.string.Matcher_Description_Login)
        is Password -> stringResource(Res.string.Matcher_Description_Password)
        is CVV -> stringResource(Res.string.Matcher_Description_CVV)
        is IP -> stringResource(Res.string.Matcher_Description_IP)
        is IPv6 -> stringResource(Res.string.Matcher_Description_IPv6)
        is CodeDetectFun -> stringResource(Res.string.Matcher_Description_Code)
        is CertDetectFun -> stringResource(Res.string.Matcher_Description_Cert)
        is UserSignature -> stringResource(Res.string.Matcher_UserSignature_Title)
        is RKNDomainDetectFun -> stringResource(Res.string.Matcher_Description_DetectBlockedDomains)
        else -> this.name
    }
}

suspend fun IMatcher.readableName(): String {
    return when (this) {
        is FullName -> getString(Res.string.Matcher_Name)
        is Email -> getString(Res.string.Matcher_Emails)
        is Phone -> getString(Res.string.Matcher_Phones)
        is CardNumber -> getString(Res.string.Matcher_CardNumbers)
        is CarNumber -> getString(Res.string.Matcher_CarNumber)
        is SNILS -> getString(Res.string.Matcher_SNILS)
        is Passport -> getString(Res.string.Matcher_Passport)
        is OMS -> getString(Res.string.Matcher_OMS)
        is INN -> getString(Res.string.Matcher_INN)
        is AccountNumber -> getString(Res.string.Matcher_AccountNumber)
        is Address -> getString(Res.string.Matcher_Address)
        is ValuableInfo -> getString(Res.string.Matcher_ValuableInfo)
        is Login -> getString(Res.string.Matcher_Login)
        is Password -> getString(Res.string.Matcher_Password)
        is CVV -> getString(Res.string.Matcher_CVV)
        is IP -> getString(Res.string.Matcher_IP)
        is IPv6 -> getString(Res.string.Matcher_IPv6)
        is CodeDetectFun -> getString(Res.string.Matcher_Code)
        is CertDetectFun -> getString(Res.string.Matcher_Cert)
        is RKNDomainDetectFun -> getString(Res.string.Matcher_DetectBlockedDomains)
        else -> this.name
    }
}
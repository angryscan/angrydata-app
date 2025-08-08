package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import info.downdetector.bigdatascanner.common.DetectFunction
import info.downdetector.bigdatascanner.common.IDetectFunction
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.DetectFunction_Cert
import ru.packetdima.datascanner.resources.DetectFunction_Code
import ru.packetdima.datascanner.resources.DetectFunction_Description_Cert
import ru.packetdima.datascanner.resources.DetectFunction_Description_Code
import ru.packetdima.datascanner.resources.DetectFunction_Description_DetectBlockedDomains
import ru.packetdima.datascanner.resources.DetectFunction_DetectBlockedDomains
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.Signature_Title
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun
import ru.packetdima.datascanner.scan.functions.RKNDomainDetectFun
import ru.packetdima.datascanner.scan.functions.UserSignature

@Composable
fun IDetectFunction.composableName(): String {
    return when (this) {
        is DetectFunction -> this.composableName()
        is CodeDetectFun -> stringResource(Res.string.DetectFunction_Code)
        is CertDetectFun -> stringResource(Res.string.DetectFunction_Cert)
        is RKNDomainDetectFun -> stringResource(Res.string.DetectFunction_DetectBlockedDomains)
        else -> this.writeName
    }
}

@Composable
fun IDetectFunction.description(): String {
    return when (this) {
        is DetectFunction -> this.description()
        is CodeDetectFun -> stringResource(Res.string.DetectFunction_Description_Code)
        is CertDetectFun -> stringResource(Res.string.DetectFunction_Description_Cert)
        is UserSignature -> stringResource(Res.string.Signature_Title)
        is RKNDomainDetectFun -> stringResource(Res.string.DetectFunction_Description_DetectBlockedDomains)
        else -> this.writeName
    }
}

suspend fun IDetectFunction.readableName(): String {
    return when (this) {
        is DetectFunction -> this.readableName()
        is CodeDetectFun -> getString(Res.string.DetectFunction_Code)
        is CertDetectFun -> getString(Res.string.DetectFunction_Cert)
        is RKNDomainDetectFun -> getString(Res.string.DetectFunction_DetectBlockedDomains)
        else -> this.writeName
    }
}
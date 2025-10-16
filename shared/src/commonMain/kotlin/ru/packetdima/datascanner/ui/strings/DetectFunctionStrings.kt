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
    DetectFunction.BirthCert to Pair(Res.string.DetectFunction_BirthCert, Res.string.DetectFunction_Description_BirthCert),
    DetectFunction.ForeignPassport to Pair(Res.string.DetectFunction_ForeignPassport, Res.string.DetectFunction_Description_ForeignPassport),
    DetectFunction.RefugeeCert to Pair(Res.string.DetectFunction_RefugeeCert, Res.string.DetectFunction_Description_RefugeeCert),
    DetectFunction.ResidencePermit to Pair(Res.string.DetectFunction_ResidencePermit, Res.string.DetectFunction_Description_ResidencePermit),
    DetectFunction.TemporaryID to Pair(Res.string.DetectFunction_TemporaryID, Res.string.DetectFunction_Description_TemporaryID),
    DetectFunction.MilitaryID to Pair(Res.string.DetectFunction_MilitaryID, Res.string.DetectFunction_Description_MilitaryID),
    DetectFunction.DriverLicense to Pair(Res.string.DetectFunction_DriverLicense, Res.string.DetectFunction_Description_DriverLicense),
    DetectFunction.ForeignTIN to Pair(Res.string.DetectFunction_ForeignTIN, Res.string.DetectFunction_Description_ForeignTIN),
    DetectFunction.EducationDoc to Pair(Res.string.DetectFunction_EducationDoc, Res.string.DetectFunction_Description_EducationDoc),
    DetectFunction.MarriageCert to Pair(Res.string.DetectFunction_MarriageCert, Res.string.DetectFunction_Description_MarriageCert),
    DetectFunction.InheritanceDoc to Pair(Res.string.DetectFunction_InheritanceDoc, Res.string.DetectFunction_Description_InheritanceDoc),
    DetectFunction.OGRNIP to Pair(Res.string.DetectFunction_OGRNIP, Res.string.DetectFunction_Description_OGRNIP),
    DetectFunction.OSAGOPolicy to Pair(Res.string.DetectFunction_OSAGOPolicy, Res.string.DetectFunction_Description_OSAGOPolicy),
    DetectFunction.SecurityAffiliation to Pair(Res.string.DetectFunction_SecurityAffiliation, Res.string.DetectFunction_Description_SecurityAffiliation),
    DetectFunction.MilitaryRank to Pair(Res.string.DetectFunction_MilitaryRank, Res.string.DetectFunction_Description_MilitaryRank),
    DetectFunction.EpCertificateNumber to Pair(Res.string.DetectFunction_EpCertificateNumber, Res.string.DetectFunction_Description_EpCertificateNumber),
    DetectFunction.CadastralNumber to Pair(Res.string.DetectFunction_CadastralNumber, Res.string.DetectFunction_Description_CadastralNumber),
    DetectFunction.VIN to Pair(Res.string.DetectFunction_VIN, Res.string.DetectFunction_Description_VIN),
    DetectFunction.VehicleRegNumber to Pair(Res.string.DetectFunction_VehicleRegNumber, Res.string.DetectFunction_Description_VehicleRegNumber),
    DetectFunction.SocialUserId to Pair(Res.string.DetectFunction_SocialUserId, Res.string.DetectFunction_Description_SocialUserId),
    DetectFunction.LegalEntityName to Pair(Res.string.DetectFunction_LegalEntityName, Res.string.DetectFunction_Description_LegalEntityName),
    DetectFunction.LegalEntityId to Pair(Res.string.DetectFunction_LegalEntityId, Res.string.DetectFunction_Description_LegalEntityId),
    DetectFunction.OKPO to Pair(Res.string.DetectFunction_OKPO, Res.string.DetectFunction_Description_OKPO),
    DetectFunction.StateRegContract to Pair(Res.string.DetectFunction_StateRegContract, Res.string.DetectFunction_Description_StateRegContract),
    DetectFunction.UidContractBank to Pair(Res.string.DetectFunction_UidContractBank, Res.string.DetectFunction_Description_UidContractBank),
    DetectFunction.ExecDocNumber to Pair(Res.string.DetectFunction_ExecDocNumber, Res.string.DetectFunction_Description_ExecDocNumber),
    DetectFunction.BankAccount to Pair(Res.string.DetectFunction_BankAccount, Res.string.DetectFunction_Description_BankAccount),
    DetectFunction.SberBook to Pair(Res.string.DetectFunction_SberBook, Res.string.DetectFunction_Description_SberBook),
    DetectFunction.BankAccountLE to Pair(Res.string.DetectFunction_BankAccountLE, Res.string.DetectFunction_Description_BankAccountLE),
    DetectFunction.HashData to Pair(Res.string.DetectFunction_HashData, Res.string.DetectFunction_Description_HashData),
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
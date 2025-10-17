package ru.packetdima.datascanner.serializers

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.angryscan.common.engine.IMatcher
import org.angryscan.common.engine.IScanEngine
import org.angryscan.common.engine.custom.CustomEngine
import org.angryscan.common.engine.hyperscan.HyperScanEngine
import org.angryscan.common.engine.kotlin.KotlinEngine
import org.angryscan.common.matchers.*
import ru.packetdima.datascanner.scan.common.connectors.ConnectorFileShare
import ru.packetdima.datascanner.scan.common.connectors.ConnectorHTTP
import ru.packetdima.datascanner.scan.common.connectors.ConnectorS3
import ru.packetdima.datascanner.scan.common.connectors.IConnector
import ru.packetdima.datascanner.scan.functions.CertDetectFun
import ru.packetdima.datascanner.scan.functions.CodeDetectFun
import ru.packetdima.datascanner.scan.functions.RKNDomainDetectFun

val PolymorphicSerializationModule = SerializersModule {
    polymorphic(IMatcher::class) {
        subclass(AccountNumber::class)
        subclass(Address::class)
        subclass(BankAccount::class)
        subclass(BankAccountLE::class)
        subclass(BirthCert::class)
        subclass(Birthday::class)
        subclass(CadastralNumber::class)
        subclass(CardNumber::class)
        subclass(CarNumber::class)
        subclass(CVV::class)
        subclass(DeathDate::class)
        subclass(DriverLicense::class)
        subclass(EducationDoc::class)
        subclass(EducationLevel::class)
        subclass(EducationLicense::class)
        subclass(Email::class)
        subclass(EpCertificateNumber::class)
        subclass(ExecDocNumber::class)
        subclass(ForeignPassports::class)
        subclass(ForeignTIN::class)
        subclass(FullName::class)
        subclass(Geo::class)
        subclass(HashData::class)
        subclass(IdentityDocType::class)
        subclass(INN::class)
        subclass(InheritanceDoc::class)
        subclass(IPv4::class)
        subclass(IPv6::class)
        subclass(LegalEntityId::class)
        subclass(LegalEntityName::class)
        subclass(Login::class)
        subclass(MaritalStatus::class)
        subclass(MarriageCert::class)
        subclass(MilitaryID::class)
        subclass(MilitaryRank::class)
        subclass(OGRNIP::class)
        subclass(OKPO::class)
        subclass(OMS::class)
        subclass(OSAGOPolicy::class)
        subclass(Passport::class)
        subclass(Password::class)
        subclass(Phone::class)
        subclass(RefugeeCert::class)
        subclass(ResidencePermit::class)
        subclass(SberBook::class)
        subclass(SecurityAffiliation::class)
        subclass(SNILS::class)
        subclass(SocialUserId::class)
        subclass(StateRegContract::class)
        subclass(TemporaryID::class)
        subclass(UidContractBankBki::class)
        subclass(VIN::class)
        subclass(VehicleRegNumber::class)

        subclass(UserSignature::class)

        subclass(CertDetectFun::class)
        subclass(CodeDetectFun::class)
        subclass(RKNDomainDetectFun::class)
    }
    polymorphic(IConnector::class) {
        subclass(ConnectorS3::class)
        subclass(ConnectorFileShare::class)
        subclass(ConnectorHTTP::class)
    }
    polymorphic(IScanEngine::class) {
        subclass(KotlinEngine::class)
        subclass(HyperScanEngine::class)
        subclass(CustomEngine::class)
    }
}

val PolymorphicFormatter = Json {
    prettyPrint = false
    serializersModule = PolymorphicSerializationModule
}
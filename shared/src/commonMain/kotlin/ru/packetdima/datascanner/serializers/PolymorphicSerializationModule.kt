package ru.packetdima.datascanner.serializers

import info.downdetector.bigdatascanner.common.DetectFunction
import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import ru.packetdima.datascanner.scan.functions.UserSignature

val PolymorphicSerializationModule = SerializersModule {
    polymorphic(IDetectFunction::class) {
        subclass(DetectFunction::class)
        subclass(UserSignature::class)
    }
}

val PolymorphicFormatter = Json {
    prettyPrint = false
    serializersModule = PolymorphicSerializationModule
}
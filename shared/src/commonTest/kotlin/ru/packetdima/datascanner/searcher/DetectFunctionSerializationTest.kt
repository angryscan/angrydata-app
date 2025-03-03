package ru.packetdima.datascanner.searcher

import info.downdetector.bigdatascanner.common.DetectFunction
import info.downdetector.bigdatascanner.common.IDetectFunction
import kotlinx.serialization.json.Json
import ru.packetdima.datascanner.serializers.PolymorphicFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class DetectFunctionSerializationTest {
    @Test
    fun `Mutable list serialization`() {
        val list:MutableList<IDetectFunction> = DetectFunction.entries.toMutableList()
        val serialized = PolymorphicFormatter.encodeToString(list)
        assertEquals(list, PolymorphicFormatter.decodeFromString(serialized))
    }

    @Test
    fun `Single detect function test`() {
        val df: IDetectFunction = DetectFunction.Name
        val serialized = PolymorphicFormatter.encodeToString(df)
        assertEquals(df, PolymorphicFormatter.decodeFromString(serialized))
    }

    @Test
    fun `Standart serialization fails`() {
        val df: IDetectFunction = DetectFunction.Name
        val formatter = Json { prettyPrint = false }
        assertFails {
            formatter.encodeToString(df)
        }

    }
}
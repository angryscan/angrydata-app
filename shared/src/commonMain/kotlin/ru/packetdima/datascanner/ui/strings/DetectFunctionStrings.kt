package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import info.downdetector.bigdatascanner.common.DetectFunction
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.resources.*

suspend fun DetectFunction.readableName(): String {
    return getString(resource("DetectFunction_${writeName}"))
}

@Composable
fun DetectFunction.composableName(): String {
    return stringResource(resource("DetectFunction_${writeName}"))
}

@Composable
fun DetectFunction.description(): String {
    return stringResource(resource("DetectFunction_Description_${writeName}"))
}

private fun resource(resourceName: String): StringResource {
    return try {
        val field = Res.string::class.java.getDeclaredField(resourceName)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.get(null) as? StringResource) ?: throw IllegalStateException("Resource $resourceName not initialized")
    } catch (e: Exception) {
        throw IllegalStateException("Resource $resourceName not found", e)
    }
}
package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.ui.datatable.TableHeader
import ru.packetdima.datascanner.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TableHeader.uiText(): String = when (this.text) {
    "File name" -> stringResource(Res.string.colPath)
    "Score" -> stringResource(Res.string.colScore)
    "Attributes count"  -> stringResource(Res.string.colCount)
    "Attributes found" -> stringResource(Res.string.colAttributes)
    "File size" -> stringResource(Res.string.colFileSize)
    else -> this.text
}
package ru.packetdima.datascanner.ui.strings

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.ThemeType_Dark
import ru.packetdima.datascanner.resources.ThemeType_Light
import ru.packetdima.datascanner.resources.ThemeType_System


/**
 * Returns a human-readable name for the theme type.
 *
 * @return A string name for the theme type.
 */
@Suppress("Unused")
suspend fun AppSettings.ThemeType.readableName(): String = when (this) {
    AppSettings.ThemeType.Dark -> getString(Res.string.ThemeType_Dark)
    AppSettings.ThemeType.Light -> getString(Res.string.ThemeType_Light)
    AppSettings.ThemeType.System -> getString(Res.string.ThemeType_System)
}

/**
 * A composable version of [readableName].
 *
 * @return A string name for the theme type.
 */
@Composable
fun AppSettings.ThemeType.composableName(): String = when (this) {
    AppSettings.ThemeType.Dark -> stringResource(Res.string.ThemeType_Dark)
    AppSettings.ThemeType.Light -> stringResource(Res.string.ThemeType_Light)
    AppSettings.ThemeType.System -> stringResource(Res.string.ThemeType_System)
}
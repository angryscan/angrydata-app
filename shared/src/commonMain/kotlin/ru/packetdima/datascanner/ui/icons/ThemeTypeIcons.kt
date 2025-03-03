package ru.packetdima.datascanner.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.resources.Res
import ru.packetdima.datascanner.resources.Theme_Dark
import ru.packetdima.datascanner.resources.Theme_Light
import ru.packetdima.datascanner.resources.Theme_System

@Composable
fun AppSettings.ThemeType.icon(): Painter = when (this) {
    AppSettings.ThemeType.Dark -> painterResource(Res.drawable.Theme_Dark)
    AppSettings.ThemeType.Light -> painterResource(Res.drawable.Theme_Light)
    AppSettings.ThemeType.System -> painterResource(Res.drawable.Theme_System)
}
package ru.packetdima.datascanner.ui.windows.screens.main.settings.items

import org.angryscan.common.engine.IMatcher

data class MatchersGroup(
    val name: String,
    val matchers: List<IMatcher>
)

fun isGroupFullySelected(
    group: MatchersGroup,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
): Boolean {
    return group.matchers.all { matcher ->
        scanSettings.matchers.any {
            it::class == matcher::class
        }
    }
}

fun isGroupPartiallySelected(
    group: MatchersGroup,
    scanSettings: ru.packetdima.datascanner.common.ScanSettings
): Boolean {
    return group.matchers.any { matcher ->
        scanSettings.matchers.any {
            it::class == matcher::class
        }
    } && !isGroupFullySelected(group, scanSettings)
}
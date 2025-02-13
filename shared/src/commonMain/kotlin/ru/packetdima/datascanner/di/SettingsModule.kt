package ru.packetdima.datascanner.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings

val settingsModule = module {
    single { AppSettings.AppSettingsFile(AppFiles.AppSettingsFile)}
    singleOf(::AppSettings)
    single { ScanSettings.ScanSettingsFile(AppFiles.ScanSettingsFile)}
    singleOf(::ScanSettings)
}
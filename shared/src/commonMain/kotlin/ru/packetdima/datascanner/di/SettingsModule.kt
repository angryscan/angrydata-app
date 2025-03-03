package ru.packetdima.datascanner.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.common.AppSettings
import ru.packetdima.datascanner.common.ScanSettings
import ru.packetdima.datascanner.common.UserSignatureSettings

val settingsModule = module {
    single { UserSignatureSettings.SettingsFile(AppFiles.UserSignaturesFiles) }
    singleOf(::UserSignatureSettings)
    single { AppSettings.AppSettingsFile(AppFiles.AppSettingsFile) }
    singleOf(::AppSettings)
    single { ScanSettings.SettingsFile(AppFiles.ScanSettingsFile) }
    singleOf(::ScanSettings)
}
package ru.packetdima.datascanner.di

import org.koin.dsl.module
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.db.DatabaseSettings

val databaseModule = module {
    single {
        DatabaseSettings(
            url = "jdbc:sqlite:${AppFiles.WorkDir.resolve("bds.db").absolutePath}",
            driver = "org.sqlite.JDBC"
        )
    }
}
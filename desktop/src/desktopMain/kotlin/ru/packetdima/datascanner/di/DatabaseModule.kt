package ru.packetdima.datascanner.di

import org.koin.dsl.module
import ru.packetdima.datascanner.common.AppFiles
import ru.packetdima.datascanner.db.DatabaseSettings

val databaseModule = module {
    single {
        DatabaseSettings(
            url = "jdbc:sqlite:${AppFiles.WorkDir.resolve("ads.db").absolutePath}",
            driver = "org.sqlite.JDBC"
        )
    }
}

val consoldeDatabaseModule = module {
    single {
        DatabaseSettings(
            url = "jdbc:sqlite:${AppFiles.WorkDir.resolve("console.db").absolutePath}",
            driver = "org.sqlite.JDBC"
        )
    }
}
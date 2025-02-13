package ru.packetdima.datascanner.di

import org.koin.dsl.module
import ru.packetdima.datascanner.db.DatabaseConnector

val scanModule = module {
    single { DatabaseConnector(get()) }
}
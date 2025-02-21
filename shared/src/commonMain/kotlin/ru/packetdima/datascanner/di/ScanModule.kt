package ru.packetdima.datascanner.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.scan.TasksViewModel
import ru.packetdima.datascanner.scan.ScanService

val scanModule = module {
    singleOf(::DatabaseConnector)
    singleOf(::TasksViewModel)
    singleOf(::ScanService)
}
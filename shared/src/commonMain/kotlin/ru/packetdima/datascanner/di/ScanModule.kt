package ru.packetdima.datascanner.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.packetdima.datascanner.db.DatabaseConnector
import ru.packetdima.datascanner.scan.ScanService
import ru.packetdima.datascanner.scan.TaskFilesViewModel
import ru.packetdima.datascanner.scan.TasksViewModel
import ru.packetdima.datascanner.scan.functions.rkn.DomainRepository

val scanModule = module {
    singleOf(::DatabaseConnector)
    singleOf(::TasksViewModel)
    singleOf(::ScanService)
    singleOf(::DomainRepository)
    factoryOf(::TaskFilesViewModel)
}
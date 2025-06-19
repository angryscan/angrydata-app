package ru.packetdima.datascanner.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.packetdima.datascanner.scan.common.connectors.S3ViewModel

val s3Module = module {
    viewModelOf(::S3ViewModel)
}
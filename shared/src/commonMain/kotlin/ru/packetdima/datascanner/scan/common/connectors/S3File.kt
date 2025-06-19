package ru.packetdima.datascanner.scan.common.connectors

data class S3File(
    val name: String,
    val path: String,
    val size: Long,
    val isDirectory: Boolean,
)

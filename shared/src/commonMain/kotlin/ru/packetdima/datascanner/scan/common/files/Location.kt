package ru.packetdima.datascanner.scan.common.files

import info.downdetector.bigdatascanner.common.extensions.MatchWithContext

data class Location(
    val entry: MatchWithContext,
    val location: String,
    val leftContext: String = "",
    val rightContext: String = ""
)
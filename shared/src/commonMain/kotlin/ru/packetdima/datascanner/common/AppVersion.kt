package ru.packetdima.datascanner.common

val AppVersion =
    System.getProperty("jpackage.app-version") ?:
    System.getProperty("app.version") ?:
    "Debug"
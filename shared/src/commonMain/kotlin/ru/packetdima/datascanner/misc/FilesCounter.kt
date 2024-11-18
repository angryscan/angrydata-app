package ru.packetdima.datascanner.misc

class FilesCounter {
    var totalFilesCount: Long = 0
    var totalFilesSize: FileSize = FileSize()
    var selectedFilesCount: Long = 0
    var selectedFilesSize: FileSize = FileSize()
    var scannedFilesCount: Long = 0
    var scannedFilesSize: FileSize = FileSize()
    var valuebleFilesCount: Long = 0
    var valuebleFilesSize: FileSize = FileSize()
    var skippedScanFilesCount: Long = 0
    var skippedScanFilesSize: FileSize = FileSize()
}
package ru.packetdima.datascanner.misc

class FileSize {
    private var bytes: Long = 0
    private var kBytes: Long = 0
    private var mBytes: Long = 0
    private var gBytes: Long = 0
    private var tBytes: Long = 0

    operator fun plus(incrementBytes: Long): FileSize {
        bytes += incrementBytes

        kBytes += bytes / 1024
        bytes %= 1024

        mBytes += kBytes / 1024
        kBytes %= 1024

        gBytes += mBytes / 1024
        mBytes %= 1024

        tBytes += gBytes / 1024
        gBytes %= 1024

        return this
    }

    operator fun plus(incrementBytes: Int): FileSize {
        return plus(incrementBytes.toLong())
    }

    override fun toString(): String {
        return when {
            tBytes != 0L -> "${tBytes}.${(gBytes * 100) / 1024} TB"
            gBytes != 0L -> "${gBytes}.${(mBytes * 100) / 1024} GB"
            mBytes != 0L -> "${mBytes}.${(kBytes * 100) / 1024} MB"
            kBytes != 0L -> "${kBytes}.${(bytes * 100) / 1024} KB"
            else -> "$bytes B"
        }
    }
}
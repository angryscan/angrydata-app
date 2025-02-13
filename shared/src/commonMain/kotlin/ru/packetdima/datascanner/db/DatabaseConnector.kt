package ru.packetdima.datascanner.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DatabaseConnector(dbSettings: DatabaseSettings) {
    val connection: Database = Database.connect(
        url = dbSettings.url,
        driver = dbSettings.driver
    )

    private val context = Dispatchers.IO.limitedParallelism(1)

    suspend fun <T> transaction(block: suspend () -> T): T =
        newSuspendedTransaction(context) { block() }
}

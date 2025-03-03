package ru.packetdima.datascanner.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DatabaseConnector: KoinComponent {
    private val dbSettings: DatabaseSettings by inject()

    val connection: Database = Database.connect(
        url = dbSettings.url,
        driver = dbSettings.driver
    )

    private val context = Dispatchers.IO.limitedParallelism(1)

    suspend fun <T> transaction(block: suspend () -> T): T =
        newSuspendedTransaction(context) { block() }
}

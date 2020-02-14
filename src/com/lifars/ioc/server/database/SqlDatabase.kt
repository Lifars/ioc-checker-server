package com.lifars.ioc.server.database

import com.lifars.ioc.server.database.tables.sql.*
import com.lifars.ioc.server.database.tables.sql.auxiliary.FoundIocs
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionInterface
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.vendors.DatabaseDialect

class Database @KtorExperimentalAPI constructor(
    config: ApplicationConfig
) {
    companion object{
        val tables = arrayOf(
            Users,
            Iocs,
            IocSearchResults,
            IocSearchErrors,
            ProbeReports,
            Probes,
            FoundIocs,
            FeedSources
        )

        val tablesByName by lazy {
            tables.associateBy { it.tableName }
        }

    }

    init {
        org.jetbrains.exposed.sql.Database.connect(hikari(config))
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                *tables
            )
        }
    }

    private fun hikari(config: ApplicationConfig): HikariDataSource {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = config.propertyOrNull("db.driver")?.getString() ?: "org.h2.Driver"
        hikariConfig.jdbcUrl = config.propertyOrNull("db.jdbcUrl")?.getString() ?: "jdbc:h2:mem:test"
        hikariConfig.username = config.propertyOrNull("db.username")?.getString()
        hikariConfig.password = config.propertyOrNull("db.password")?.getString()
        hikariConfig.maximumPoolSize = 3
        hikariConfig.isAutoCommit = false
        hikariConfig.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        hikariConfig.validate()
        return HikariDataSource(hikariConfig)
    }

    suspend fun <T> query(block: suspend CoroutineTransaction.() -> T): T = withContext(Dispatchers.IO) {
        transaction {
            runBlocking {
                CoroutineTransaction(this@transaction, this).block()
            }
        }
    }
}

class CoroutineTransaction(impl: Transaction, scope: CoroutineScope) : TransactionInterface by impl,
    CoroutineScope by scope
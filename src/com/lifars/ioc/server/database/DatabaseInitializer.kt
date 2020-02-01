package com.lifars.ioc.server.database

import com.lifars.ioc.server.database.entities.IocEntry
import com.lifars.ioc.server.database.tables.Iocs
import com.lifars.ioc.server.database.tables.Probes
import com.lifars.ioc.server.database.tables.Users
import com.lifars.ioc.server.security.PasswordHasher
import com.lifars.ioc.server.serialization.json
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select

private val logger = KotlinLogging.logger { }

data class InitAdmin(
    val email: String,
    val passwordPlain: String
)

data class InitProbe(
    val name: String,
    val apiKey: String
)

data class DatabaseInitializer(
    private val database: Database,
    private val userPasswordHasher: PasswordHasher,
    private val probePasswordHasher: PasswordHasher,
    private val admin: InitAdmin?,
    private val probe: InitProbe?,
    private val testIoc: Boolean
) {
    fun initializeDatabase() = runBlocking {
        logger.info { "Initializing database data." }
        database.query {
            val adminId = if (admin != null) {
                Users.select {
                    Users.role eq Users.Role.ADMIN
                }.limit(1)
                    .firstOrNull()
                    ?.let { it[Users.id] }
                    ?.let { existingAdminId ->
                        Users.deleteWhere { Users.id eq existingAdminId }
                    }

                Users.insertAndGetId { row ->
                    row[role] = Users.Role.ADMIN
                    row[company] = ""
                    row[email] = admin.email
                    row[name] = admin.email.substringBefore("@")
                    row[username] = admin.email.substringBefore("@")
                    row[password] = userPasswordHasher.hash(admin.passwordPlain)
                }
            } else {
                Users.select {
                    Users.role eq Users.Role.ADMIN
                }.limit(1).firstOrNull()?.let { it[Users.id] } ?: throw AdminNotSetException()
            }
            logger.debug { "New admin with id $adminId created." }

            probe?.let {
                Probes.insertAndGetId { row ->
                    row[name] = probe.name
                    row[apiKey] = probePasswordHasher.hash(probe.apiKey)
                    row[owner] = adminId
                    row[registeredBy] = adminId
                }.let { logger.debug { "New probe with id ${it.value} created for user with id $adminId" } }
            }

            if(testIoc){
                Iocs.insert {row ->
                    row[name] = "StarWars"
                    row[definition] = IocEntry(
                        name = "DarthVader",
                        offspring = listOf(
                            IocEntry(
                                name = "LukeSkywalker",
                                fileCheck = IocEntry.FileInfo(
                                    name = "C:/Users/IEUser/Documents/Luke.txt"
                                )
                            ),
                            IocEntry(
                                name = "LeiaOrgana",
                                fileCheck = IocEntry.FileInfo(
                                    name = "C:/Users/IEUser/Documents/Leia.txt",
                                    hash = IocEntry.Hashed(
                                        algorithm = IocEntry.Hashed.Type.MD5,
                                        value = "85076E14BFEA8BC12EE01FAE12EA77F9"
                                    )
                                )
                            )
                        )
                    ).json()
                }
            }
        }
    }

}

class AdminNotSetException :
    RuntimeException("Admin account do not exist. Set it up with command line arguments. Run this app with --help argument to more info.")



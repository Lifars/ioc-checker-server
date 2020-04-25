package com.lifars.ioc.server.database

import com.lifars.ioc.server.database.entities.IocEntry
import com.lifars.ioc.server.database.tables.sql.FeedSources
import com.lifars.ioc.server.database.tables.sql.Iocs
import com.lifars.ioc.server.database.tables.sql.Probes
import com.lifars.ioc.server.database.tables.sql.Users
import com.lifars.ioc.server.security.PasswordHasher
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*

private val logger = KotlinLogging.logger { }

data class InitUser(
    val email: String
)

data class InitProbe(
    val name: String,
    val apiKey: String
)

data class DatabaseInitializer(
    private val database: Database,
    private val probePasswordHasher: PasswordHasher,
    private val admin: InitUser?,
    private val probe: InitProbe?,
    private val testIoc: Boolean,
    private val registerFeedSources: Boolean
) {
    fun initializeDatabase() = runBlocking {
        logger.info { "Initializing database data." }
        database.query {
            val adminId = if (admin != null) {
                createAdmin(admin)
            } else {
                Users.select {
                    Users.role eq Users.Role.SMURF_ADMIN
                }.limit(1).firstOrNull()?.let { it[Users.id] } ?: throw AdminNotSetException()
            }

            logger.debug { "New admin with id $adminId created." }
            probe?.let {
                createProbeForAdmin(probe, adminId)
            }
            if (testIoc) {
                createDummyIoc()
            }
            if(registerFeedSources){
                createDefaultFeedSources()
            }
        }
    }

    private fun createDefaultFeedSources() {
        listOf(
            "https://www.circl.lu/doc/misp/feed-osint/",
            "https://www.botvrij.eu/data/feed-osint/?C=M;O=D"
        ).let { feedSources->
            FeedSources.batchInsert(feedSources) { source ->
                this[FeedSources.url] = source
                this[FeedSources.type] = FeedSources.FeedType.MISP
            }
        }
    }

    private fun createDummyIoc() {
        Iocs.insert { row ->
            row[name] = "StarWars"
            row[definition] = IocEntry(
                name = "DarthVader",
                offspring = listOf(
                    IocEntry(
                        name = "LukeSkywalker",
                        fileCheck = IocEntry.FileInfo(
                            name = "%UserProfile%/Documents/Luke.txt"
                        )
                    ),
                    IocEntry(
                        name = "LeiaOrgana",
                        fileCheck = IocEntry.FileInfo(
                            name = "%UserProfile%/Documents/Leia.txt",
                            hash = IocEntry.Hashed(
                                algorithm = IocEntry.Hashed.Type.MD5,
                                value = "85076E14BFEA8BC12EE01FAE12EA77F9"
                            )
                        )
                    )
                )
            )
        }
        Iocs.insert { row ->
            row[name] = "StarTrek"
            row[definition] = IocEntry(
                name = "CaptainPicard",
                dnsCheck = IocEntry.DnsInfo(
                    name = "teredo.ipv6.microsoft.com" // ModernIE Windows 7 has such dns entry
                ),
                registryCheck = IocEntry.RegistryInfo(
                    key = "HKEY_CURRENT_USER\\AppEvents\\EventLabels\\ActivatingDocument",
                    valueName = "DispFileName",
                    value = "@ieframe.dll,-10321"
                ),
                certsCheck = IocEntry.CertsInfo(
                    name = "VeriSign"
                ),
                connsCheck = IocEntry.ConnsInfo(
                    search = IocEntry.SearchType.REGEX,
                    name = "\\.com"
                ),
                processCheck = IocEntry.ProcessInfo(
                    IocEntry.SearchType.EXACT,
                    name = "svchost.exe"
                )
            )
        }
    }

    private fun createProbeForAdmin(
        probe: InitProbe,
        adminId: EntityID<Long>
    ) {
        Probes.insertAndGetId { row ->
            row[name] = probe.name
            row[apiKey] = probePasswordHasher.hash(probe.apiKey)
            row[owner] = adminId
            row[registeredBy] = adminId
        }.let {
            logger.debug { "New probe with id ${it.value} created for user with id $adminId" }
        }
    }

    private fun createAdmin(admin: InitUser): EntityID<Long> {
        Users.select {
            Users.role eq Users.Role.SMURF_ADMIN
        }.limit(1)
            .firstOrNull()
            ?.let { it[Users.id] }
            ?.let { existingAdminId ->
                Users.deleteWhere { Users.id eq existingAdminId }
            }

        return Users.insertAndGetId { row ->
            row[role] = Users.Role.SMURF_ADMIN
            row[email] = admin.email
        }
    }

    private fun createNormalUser(user: InitUser): EntityID<Long> {
        return Users.insertAndGetId { row ->
            row[role] = Users.Role.IOC_CHECKER
            row[email] = user.email
        }
    }
}

class AdminNotSetException :
    RuntimeException("Admin account do not exist. Set it up with command line arguments. Run this app with --help argument to more info.")



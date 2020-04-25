package com.lifars.ioc.server.config

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.repository.keyvalue.KeyValueVisitedFeedUrlRepository
import com.lifars.ioc.server.database.repository.sql.*
import com.lifars.ioc.server.database.tables.keyvalue.VisitedFeeds
import com.lifars.ioc.server.security.PasswordHasher
import com.lifars.ioc.server.serialization.additionalSetup
import com.lifars.ioc.server.service.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.config.ApplicationConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File
import java.net.URL

val probeQualifier = named("probeQualifier")
//val userQualifier = named("userQualifier")

val probeRealm = named("probeRealm")
val userRealm = named("userRealm")
val jwkIssuer = named("jwkIssuer")

fun mainDiModule(
    applicationConfig: ApplicationConfig
) = module {

    factory {
        HttpClient(CIO) {
            install(JsonFeature) {
                serializer = JacksonSerializer {
                    additionalSetup()
                }
            }
        }
    }

    single { Database(applicationConfig) }

    single { VisitedFeeds(applicationConfig.property("feed.storage").getString().let { File(it) }) }

    single(probeQualifier) { PasswordHasher(applicationConfig.property("auth.probe.pepper").getString()) }

//    single(userQualifier) { PasswordHasher(applicationConfig.property("auth.user.pepper").getString()) }

    single(probeRealm) { applicationConfig.property("auth.probe.realm").getString() }

    single(userRealm) { applicationConfig.property("auth.user.openId.realm").getString() }

    single(jwkIssuer) { applicationConfig.property("auth.user.openId.jwkUrl").getString().let { URL(it) } }

    single<UserRepository> { SqlUserRepository(get()) }

    single<ProbeRepository> { SqlProbeRepository(get()) }

    single<IocRepository> { SqlIocRepository(get()) }

    single<FeedSourceRepository> { SqlFeedSourceRepository(get()) }

    single<FoundIocRepository> { SqlFoundIocRepository(get()) }

    single { KeyValueVisitedFeedUrlRepository(get()) }

    single<ProbeReportRepository> {
        SqlProbeReportRepository(
            database = get(),
            iocRepository = get()
        )
    }

    single {
        ProbeReportService(
            repository = get(),
            probeRepository = get()
        )
    }

    single {
        IocService(
            repository = get()
        )
    }

    single {
        ProbeService(
            repository = get(),
            passwordHasher = get(probeQualifier),
            userRepository = get()
        )
    }

    single {
        UserService(
            repository = get()
        )
    }

    single {
        FeedSourceService(
            repository = get()
        )
    }

    single {
        FeedService(
            feedSourceRepository = get(),
            iocRepository = get(),
            visitedFeedUrls = get(),
            httpClientFactory = { get() }
        )
    }

    single {
        FoundIocService(
            repository = get()
        )
    }
}
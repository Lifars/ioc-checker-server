package com.lifars.ioc.server.config

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.repository.keyvalue.KeyValueVisitedFeedUrlRepository
import com.lifars.ioc.server.database.repository.sql.*
import com.lifars.ioc.server.database.tables.keyvalue.VisitedFeeds
import com.lifars.ioc.server.security.JwtProvider
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
import java.time.Duration

val probeQualifier = named("probeQualifier")
val userQualifier = named("userQualifier")

val probeRealm = named("probeRealm")
val userRealm = named("userRealm")

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

    single(userQualifier) { PasswordHasher(applicationConfig.property("auth.user.pepper").getString()) }

    single(probeRealm) { applicationConfig.property("auth.probe.realm").getString() }

    single(userRealm) { applicationConfig.property("auth.user.realm").getString() }

    single {
        JwtProvider(
            secret = applicationConfig.property("auth.user.jwt.secret").getString(),
            audience = applicationConfig.property("auth.user.jwt.audience").getString(),
            issuer = applicationConfig.property("auth.user.jwt.issuer").getString(),
            timeout = applicationConfig.property("auth.user.jwt.timeout").getString().toLong()
                .let { Duration.ofMinutes(it) }
        )
    }

    single<UserRepository> { SqlUserRepository(get()) }

    single<ProbeRepository> { SqlProbeRepository(get()) }

    single<IocRepository> { SqlIocRepository(get()) }

    single<FeedSourceRepository> { SqlFeedSourceRepository(get()) }

    single<FoundIocRepository> { SqlFoundIoctRepository(get()) }

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
        AuthService(
            userRepository = get(),
            jwtProvider = get(),
            passwordHasher = get(userQualifier)
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
            repository = get(),
            passwordHasher = get(userQualifier)
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
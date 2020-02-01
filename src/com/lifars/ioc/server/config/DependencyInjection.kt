package com.lifars.ioc.server.config

import com.lifars.ioc.server.database.Database
import com.lifars.ioc.server.database.repository.*
import com.lifars.ioc.server.database.repository.sql.*
import com.lifars.ioc.server.security.JwtProvider
import com.lifars.ioc.server.security.PasswordHasher
import com.lifars.ioc.server.service.*
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Duration

val probeQualifier = named("probeQualifier")
val userQualifier = named("userQualifier")

val probeRealm = named("probeRealm")
val userRealm = named("userRealm")

@KtorExperimentalAPI
fun mainDiModule(
    applicationConfig: ApplicationConfig
) = module {
    single { Database(applicationConfig) }

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

    single<ProbeErrorRepository> { SqlProbeErrorRepository(get()) }

    single<ProbeOkResultRepository> { SqlProbeOkResultRepository(get()) }

    single<ProbeReportRepository> {
        SqlProbeReportRepository(
            database = get(),
            iocRepository = get(),
            probeErrorRepository = get(),
            probeOkRepository = get()
        )
    }

    single {
        ProbeReportService(
            probeReportRepository = get(),
            probeRepository = get()
        )
    }

    single {
        IocService(
            iocRepository = get()
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
            probeRepository = get(),
            passwordHasher = get(probeQualifier),
            userRepository = get()
        )
    }

    single {
        UserService(
            userRepository = get(),
            passwordHasher = get(userQualifier)
        )
    }
}
package com.lifars.ioc.server.config

import com.lifars.ioc.server.database.DatabaseInitializer
import com.lifars.ioc.server.database.InitAdmin
import com.lifars.ioc.server.database.InitProbe
import com.lifars.ioc.server.exceptions.AuthenticationException
import com.lifars.ioc.server.exceptions.AuthorizationException
import com.lifars.ioc.server.exceptions.UserAlreadyExistsException
import com.lifars.ioc.server.exceptions.UserNotFoundException
import com.lifars.ioc.server.serialization.additionalSetup
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.response.respond
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get
import org.slf4j.event.Level

internal fun Application.configure(parsedArgs: CommandLineArguments) {
    install(Locations) {
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        method(HttpMethod.Get)
        header(HttpHeaders.Authorization)
        header("Content-Range")
        header(HttpHeaders.AccessControlAllowOrigin)
        allowSameOrigin
        allowNonSimpleContentTypes = true
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
//    install(ForwardedHeaderSupport)
    install(XForwardedHeaderSupport)

    install(DataConversion)

//    // https://ktor.io/servers/features/https-redirect.html#testing
//    if (!testing) {
//        install(HttpsRedirect) {
//            // The port to redirect to. By default 443, the default HTTPS port.
//            sslPort = 443
//            // 301 Moved Permanently, or 302 Found redirect.
//            permanentRedirect = true
//        }
//    }

    install(Koin) {
        printLogger()
        modules(mainDiModule(applicationConfig = environment.config))
    }

    install(Authentication) {
        probeAuthentication(
            probeRepository = get(),
            authRealm = get(probeRealm),
            passwordHasher = get(probeQualifier)
        )
        userAuthentication(
            userRepository = get(),
            authRealm = get(userRealm),
            jwtProvider = get()
        )
    }

    install(ContentNegotiation) {
        jackson {
            additionalSetup()
        }
    }

    install(StatusPages) {
        exception<AuthenticationException> { cause ->
            call.respond(HttpStatusCode.Unauthorized)
            throw cause
        }
        exception<AuthorizationException> { cause ->
            call.respond(HttpStatusCode.Forbidden)
            throw cause
        }
        exception<UserAlreadyExistsException> { cause ->
            call.respond(HttpStatusCode.UnprocessableEntity)
            throw cause
        }
        exception<UserNotFoundException> { cause ->
            call.respond(HttpStatusCode.NotFound)
            throw cause
        }
        exception<UnsupportedOperationException> { cause ->
            call.respond(HttpStatusCode.NotFound)
            throw cause
        }
    }

    fillDefaultDatabaseRows(parsedArgs)
    installScheduledTasks()
}

private fun Application.fillDefaultDatabaseRows(parsedArgs: CommandLineArguments) {
    val initProbe = if (parsedArgs.initAdminProbeName == null) null
    else InitProbe(
        name = parsedArgs.initAdminProbeName!!,
        apiKey = parsedArgs.initAdminProbeKey!!
    )
    val initAdmin = if (parsedArgs.initAdminEmail == null) null
    else InitAdmin(
        email = parsedArgs.initAdminEmail!!,
        passwordPlain = parsedArgs.initAdminPass!!
    )

    DatabaseInitializer(
        database = get(),
        probePasswordHasher = get(probeQualifier),
        userPasswordHasher = get(userQualifier),
        probe = initProbe,
        admin = initAdmin,
        testIoc = parsedArgs.insertDummyIoc,
        registerFeedSources = parsedArgs.defaultIocFeeders
    ).initializeDatabase()
}

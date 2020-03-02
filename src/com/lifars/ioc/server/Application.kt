package com.lifars.ioc.server

import com.lifars.ioc.server.config.*
import com.lifars.ioc.server.database.entities.Ioc
import com.lifars.ioc.server.routes.*
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import mu.KotlinLogging
import org.koin.ktor.ext.get

lateinit var parsedArgs: CommandLineArguments
private val rootLogger = KotlinLogging.logger { }

fun main(args: Array<String>) = mainBody {
    Logo.print()
    parsedArgs = ArgParser(filterArgsForArgParser(args)).parseInto(::CommandLineArguments)

    // This must be last line in main
    io.ktor.server.netty.EngineMain.main(args)
}

@KtorExperimentalLocationsAPI
@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    configure(parsedArgs)

    routing {

        probeIocSearch(
            probeService = get(),
            iocService = get()
        )

        userAuth(
            authService = get()
        )

        adminProbe(
            service = get()
        )

        adminIoc(
            service = get()
        )

        adminFoundIoc(
            service = get()
        )

        adminProbeReport(
            service = get()
        )

        adminUser(
            service = get()
        )

        adminFeedSource(
            service = get()
        )

        get("/") {
            call.respondText(
                "Lifars IOC Server, version ${Ioc::class.java.`package`.implementationVersion
                    ?: "NA"}", contentType = ContentType.Text.Plain
            )
        }

        authenticate(ADMIN_AUTH) {
            get("/admin") {
                call.respondText(
                    "Lifars IOC Server, version ${Ioc::class.java.`package`.implementationVersion
                        ?: "NA"}\n\nThis is a restricted area. If you are not logged-in admin and see this message, then this app's security sucks and the programmer should be ashamed.",
                    contentType = ContentType.Text.Plain
                )
            }
        }
    }
}
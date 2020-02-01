package com.lifars.ioc.server.routes

import com.lifars.ioc.server.payload.AuthPayload
import com.lifars.ioc.server.routes.locations.ClientLocations
import com.lifars.ioc.server.service.AuthService
import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

@KtorExperimentalLocationsAPI
fun Route.userAuth(
    authService: AuthService
) {
    post<ClientLocations.Login> {
        val credentials = call.receive<AuthPayload.LoginCredentials>()
        val principal = authService.login(credentials)
        call.respond(principal)
    }
}

package com.lifars.ioc.server.routes.locations

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

object ProbeLocations {

    @KtorExperimentalLocationsAPI
    @Location("/api/probe")
    class Api {
        @KtorExperimentalLocationsAPI
        @Location("/auth")
        class Auth {
            @Location("/get/ioc/{page}")
            data class GetIocs(val page: Int)

            @Location("/post/ioc/result")
            class PostIocResult
        }
    }
}
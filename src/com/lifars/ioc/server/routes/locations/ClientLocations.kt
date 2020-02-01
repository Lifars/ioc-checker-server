package com.lifars.ioc.server.routes.locations

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
object ClientLocations {

    @Location("/api/client")
    class Api {
        @Location("/auth")
        class Auth {
            @Location("/get/ioc")
            class GetIocs

            @Location("/post/ioc/")
            class PostNewIocDefinition

            @Location("/get/ioc/reports-by-user")
            class GetIocReportsForUser

            @Location("/get/ioc/reports")
            class GetAllIocReports

            @Location("/post/probe/register")
            class RegisterNewProbe

        }

        @Location("/get/ioc")
        class GetIocs
    }

    @Location("/login")
    class Login
}
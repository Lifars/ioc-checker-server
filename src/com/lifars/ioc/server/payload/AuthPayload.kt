package com.lifars.ioc.server.payload

import java.time.Instant

object AuthPayload {
    data class LoginCredentials(
        val username: String,
        val password: String
    )

    data class RegisterCredentials(
        val name: String,
        val username: String,
        val email: String,
        val company: String,
        val password: String,
        val expires: Instant
    )
}
package com.lifars.ioc.server.payload

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

object UserPayload{
    data class User(
        val id: Long,
        val name: String,
        val username: String,
        val email: String,
        val company: String,
        val expires: Instant?,
        val created: Instant,
        val updated: Instant,
        val role: Role
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SaveUserWithPassword (
        val id: Long,
        val name: String,
        val username: String,
        val email: String,
        val company: String,
        val expires: Instant?,
        val role: Role,
        val passwordPlain: String
    )

    enum class Role {
        STANDARD,
        ADMIN
    }
}
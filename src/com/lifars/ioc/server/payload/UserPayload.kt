package com.lifars.ioc.server.payload

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

object UserPayload{

    object Request {
        data class Create(
            override val data: SaveUserWithPassword
        ) : Payload.Request.Create<SaveUserWithPassword>

        data class Update(
            override val id: Long,
            override val data: SaveUserWithPassword,
            override val previousData: SaveUserWithPassword
        ) : Payload.Request.Update<SaveUserWithPassword>

        data class UpdateMany(
            override val ids: List<Long>,
            override val data: SaveUserWithPassword
        ) : Payload.Request.UpdateMany<SaveUserWithPassword>
    }

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
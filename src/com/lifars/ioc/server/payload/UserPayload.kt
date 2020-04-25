package com.lifars.ioc.server.payload

import com.lifars.ioc.server.database.entities.User
import java.time.Instant

object UserPayload {

    object Request {
        data class Create(
            override val data: User
        ) : Payload.Request.Create<User>

        data class Update(
            override val id: Long,
            override val data: User,
            override val previousData: User
        ) : Payload.Request.Update<User>

        data class UpdateMany(
            override val ids: List<Long>,
            override val data: User
        ) : Payload.Request.UpdateMany<User>
    }

    data class User(
        val id: Long,
        val email: String,
        val created: Instant,
        val updated: Instant,
        val role: Role
    )

    enum class Role {
        STANDARD,
        SMURF_ADMIN
    }
}

fun User.Role.toPayloadRole() =
    when (this) {
        User.Role.IOC_CHECKER -> UserPayload.Role.STANDARD
        User.Role.SMURF_ADMIN -> UserPayload.Role.SMURF_ADMIN
    }

fun UserPayload.Role.toEntityRole() =
    when (this) {
        UserPayload.Role.STANDARD -> User.Role.IOC_CHECKER
        UserPayload.Role.SMURF_ADMIN -> User.Role.SMURF_ADMIN
    }

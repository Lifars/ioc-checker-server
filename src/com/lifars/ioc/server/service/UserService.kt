package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.User
import com.lifars.ioc.server.database.repository.UserRepository
import com.lifars.ioc.server.payload.Payload
import com.lifars.ioc.server.payload.UserPayload
import com.lifars.ioc.server.payload.toEntityRole
import com.lifars.ioc.server.payload.toPayloadRole
import java.lang.UnsupportedOperationException
import java.time.Instant

class UserService(
    override val repository: UserRepository
) : CrudService<User, UserPayload.User, UserPayload.User> {

    override fun User.toDto() = UserPayload.User(
        id = id,
        created = created,
        updated = updated,
        role = role.toPayloadRole(),
        email = email
    )

    override fun User.toSavedDto() = toDto()

    override fun UserPayload.User.toSaveEntity() = User(
        id = id,
        role = role.toEntityRole(),
        email = email,
        updated = Instant.now(),
        created = Instant.now()
    )

    override suspend fun save(request: Payload.Request.Create<UserPayload.User>): Payload.Response.Create<UserPayload.User> {
        throw NotImplementedError("Users cannot be modified")
    }

    override suspend fun save(request: Payload.Request.Update<UserPayload.User>): Payload.Response.Update<UserPayload.User> {
        throw NotImplementedError("Users cannot be modified")
    }

    override suspend fun save(request: Payload.Request.UpdateMany<UserPayload.User>): Payload.Response.UpdateMany {
        throw NotImplementedError("Users cannot be modified")
    }

    override suspend fun delete(request: Payload.Request.Delete): Payload.Response.Delete<UserPayload.User> {
        throw NotImplementedError("Users cannot be modified")
    }

    override suspend fun delete(request: Payload.Request.DeleteMany): Payload.Response.DeleteMany {
        throw NotImplementedError("Users cannot be modified")
    }
}
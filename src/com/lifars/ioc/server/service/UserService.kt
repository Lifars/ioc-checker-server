package com.lifars.ioc.server.service

import com.lifars.ioc.server.database.entities.UserWithPassword
import com.lifars.ioc.server.database.repository.CrudRepository
import com.lifars.ioc.server.database.repository.UserRepository
import com.lifars.ioc.server.payload.UserPayload
import com.lifars.ioc.server.security.PasswordHasher
import java.time.Instant

class UserService(
    override val repository: UserRepository,
    private val passwordHasher: PasswordHasher
) : CrudService<UserWithPassword, UserPayload.User, UserPayload.SaveUserWithPassword> {

    override fun UserWithPassword.toDto() = UserPayload.User(
        id = id,
        created = created,
        updated = updated,
        expires = expires,
        name = name,
        company = company,
        username = username,
        role = UserPayload.Role.valueOf(role.name),
        email = email
    )

    override fun UserWithPassword.toSavedDto() = UserPayload.SaveUserWithPassword(
        id = id,
        expires = expires,
        name = name,
        company = company,
        username = username,
        role = UserPayload.Role.valueOf(role.name),
        email = email,
        passwordPlain = "PASSWORD_PLACEHOLDER"
    )

    override fun UserPayload.SaveUserWithPassword.toSaveEntity() = UserWithPassword(
        id = id,
        expires = expires,
        name = name,
        company = company,
        username = username,
        role = UserWithPassword.Role.valueOf(role.name),
        email = email,
        password = passwordHasher.hash(passwordPlain),
        updated = Instant.now(),
        created = Instant.now()
    )

}
package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.UserWithPassword

interface UserRepository: CrudRepository<Long, UserWithPassword> {
    suspend fun findByEmail(email: String): UserWithPassword?

    suspend fun findByUsername(username: String): UserWithPassword?

    suspend fun findByUsernameOrEmail(usernameOrEmail: String): UserWithPassword?
}
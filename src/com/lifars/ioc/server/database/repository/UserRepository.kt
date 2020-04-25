package com.lifars.ioc.server.database.repository

import com.lifars.ioc.server.database.entities.User

interface UserRepository: CrudRepository<Long, User> {
    suspend fun findByEmail(email: String): User?

}
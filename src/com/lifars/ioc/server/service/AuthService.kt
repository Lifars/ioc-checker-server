package com.lifars.ioc.server.service

import com.lifars.ioc.server.config.UserPrincipal
import com.lifars.ioc.server.database.entities.UserWithPassword
import com.lifars.ioc.server.database.repository.UserRepository
import com.lifars.ioc.server.exceptions.AuthenticationException
import com.lifars.ioc.server.exceptions.UserAlreadyExistsException
import com.lifars.ioc.server.payload.AuthPayload
import com.lifars.ioc.server.security.JwtProvider
import com.lifars.ioc.server.security.PasswordHasher
import java.time.Instant

class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtProvider: JwtProvider
) {

    private fun authenticateUser(user: UserWithPassword, passwordPlain: String): UserPrincipal {
        val isAuthenticated = passwordHasher.verify(passwordPlain, user.password)
        if (isAuthenticated.not()) throw AuthenticationException("User ${user.email} not authenticated due to incorrect password")
        val roles = listOf(UserPrincipal.Role.valueOf(user.role.name))
        val token = jwtProvider.createJWT(user.email, roles.map { it.name }.toTypedArray())
        return UserPrincipal(user.id, user.email, token, roles)
    }

    suspend fun login(credentials: AuthPayload.LoginCredentials): UserPrincipal {
        val user = userRepository.findByUsernameOrEmail(credentials.username)
            ?: throw AuthenticationException("User ${credentials.username} not found")
        return authenticateUser(user, credentials.password)
    }

    suspend fun register(credentials: AuthPayload.RegisterCredentials): UserPrincipal {
        if (userRepository.findByUsername(credentials.username) != null)
            throw UserAlreadyExistsException("Username ${credentials.username} already exists")
        if (userRepository.findByEmail(credentials.email) != null)
            throw UserAlreadyExistsException("Email ${credentials.email} already exists")

        val hashedPassword = passwordHasher.hash(credentials.password)
        val user = userRepository.save(
            UserWithPassword(
                id = 0,
                password = hashedPassword,
                name = credentials.name,
                email = credentials.email,
                updated = Instant.now(),
                created = Instant.now(),
                role = UserWithPassword.Role.STANDARD,
                username = credentials.username,
                company = credentials.company,
                expires = credentials.expires
            )
        )
        return authenticateUser(user, credentials.password)
    }
}
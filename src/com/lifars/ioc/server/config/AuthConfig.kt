package com.lifars.ioc.server.config

import com.lifars.ioc.server.database.entities.UserWithPassword
import com.lifars.ioc.server.database.repository.ProbeRepository
import com.lifars.ioc.server.database.repository.UserRepository
import com.lifars.ioc.server.security.JwtProvider
import com.lifars.ioc.server.security.PasswordHasher
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.basic
import io.ktor.auth.jwt.JWTAuthenticationProvider
import io.ktor.auth.jwt.jwt
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

const val PROBE_AUTH = "probeAuth"
const val USER_AUTH = "userAuth"
const val ADMIN_AUTH = "adminAuth"

data class ProbePrincipal(val probeId: Long) : Principal

fun Authentication.Configuration.probeAuthentication(
    probeRepository: ProbeRepository,
    passwordHasher: PasswordHasher,
    authRealm: String
) {
    basic(PROBE_AUTH) {
        realm = authRealm
        validate { credential ->
            val probeInfo = probeRepository.findIdAndApiKeyByName(credential.name.substringAfter("Basic "))
            probeInfo?.let {
                if (passwordHasher.verify(credential.password, probeInfo.apikey)) {
                    logger.debug { "Probe with id ${probeInfo.probeId} authenticated" }
                    ProbePrincipal(probeInfo.probeId)
                } else null
            }
        }
    }
}

data class UserPrincipal(
    val id: Long,
    val email: String,
    val token: String
) : Principal

fun Authentication.Configuration.userAuthentication(
    userRepository: UserRepository,
    authRealm: String,
    jwtProvider: JwtProvider
) {
    jwt(USER_AUTH) {
        jwtAuth(authRealm, jwtProvider, userRepository)
    }

    jwt(ADMIN_AUTH) {
        jwtAuth(authRealm, jwtProvider, userRepository) { user ->
            user.role == UserWithPassword.Role.ADMIN
        }
    }
}

private fun JWTAuthenticationProvider.Configuration.jwtAuth(
    authRealm: String,
    jwtProvider: JwtProvider,
    userRepository: UserRepository,
    additionalUserConstraint: (user: UserWithPassword) -> Boolean = { true }
) {
    realm = authRealm
    verifier(jwtProvider.verifier)
//    authSchemes("token")
    validate { credential ->
        if (credential.payload.audience.contains(jwtProvider.audience).not())
            return@validate null

        val claim = "email"
        val email = credential.payload.claims[claim]?.asString() ?: return@validate null

        val user = userRepository.findByEmail(email) ?: return@validate null

        if (additionalUserConstraint(user).not()) return@validate null

        val token = jwtProvider.createJWT(email)
        UserPrincipal(id = user.id, email = email, token = token)
    }
}
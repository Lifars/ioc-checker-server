package com.lifars.ioc.server.config

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
const val FRONTEND_AUTH = "frontendAuth"

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
    val token: String,
    val roles: List<Role>
) : Principal {
    enum class Role{
        STANDARD,
        ADMIN
    }
}

fun Authentication.Configuration.userAuthentication(
    userRepository: UserRepository,
    authRealm: String,
    jwtProvider: JwtProvider
) {
    jwt(FRONTEND_AUTH) {
        jwtAuth(authRealm, jwtProvider, userRepository)
    }
}

private fun JWTAuthenticationProvider.Configuration.jwtAuth(
    authRealm: String,
    jwtProvider: JwtProvider,
    userRepository: UserRepository
) {
    realm = authRealm
    verifier(jwtProvider.verifier)
    validate { credential ->
        if (credential.payload.audience.contains(jwtProvider.audience).not())
            return@validate null

        val claim = "email"
        val email = credential.payload.claims[claim]?.asString() ?: return@validate null

        val user = userRepository.findByEmail(email) ?: return@validate null

        val token = jwtProvider.createJWT(email, arrayOf(user.role.name))
        UserPrincipal(id = user.id, email = email, token = token, roles = listOf(UserPrincipal.Role.valueOf(user.role.name)))
    }
}
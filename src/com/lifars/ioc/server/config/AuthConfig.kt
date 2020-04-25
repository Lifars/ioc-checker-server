package com.lifars.ioc.server.config

import com.auth0.jwk.JwkProviderBuilder
import com.lifars.ioc.server.database.entities.User
import com.lifars.ioc.server.database.repository.ProbeRepository
import com.lifars.ioc.server.database.repository.UserRepository
import com.lifars.ioc.server.security.PasswordHasher
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.basic
import io.ktor.auth.jwt.JWTAuthenticationProvider
import io.ktor.auth.jwt.jwt
import io.ktor.client.HttpClient
import mu.KotlinLogging
import java.net.URL
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

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
    val roles: List<User.Role>
) : Principal

fun Authentication.Configuration.userAuthentication(
    userRepository: UserRepository,
    authRealm: String,
    jwkIssuer: URL
) {
    val jwkProvider = JwkProviderBuilder(jwkIssuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
    jwt(FRONTEND_AUTH) {
        verifier(jwkProvider)
        realm = authRealm
        validate { credentials ->
            val payload = credentials.payload
            val expires = payload.expiresAt
            if (Date() > expires) {
                return@validate null
            }

            val role = run roleHandler@{
                val realmAccess = payload.getClaim("realm_access").asMap()
                val rolesObject = realmAccess["roles"]
                val rolesIterable = rolesObject as Iterable<*>
                val roles = rolesIterable.filterIsInstance<String>()

                val adminRole = roles.any { it == User.Role.SMURF_ADMIN.name }
                if(adminRole){
                    return@roleHandler User.Role.SMURF_ADMIN
                }
                val userRole = roles.any { it == User.Role.IOC_CHECKER.name }
                if(userRole){
                    return@roleHandler User.Role.IOC_CHECKER
                }else{
                    null
                }
            } ?: return@validate null

            val email = payload.getClaim("email").asString() ?: return@validate null
            val user = userRepository.findByEmail(email) ?: User(
                id = -1,
                email = email,
                role = role,
                created = Instant.now(),
                updated = Instant.now()
            )
            val userInternalId = if(user.id > 0){
                user.id
            }else{
                userRepository.create(user)
            }

            UserPrincipal(id = userInternalId, roles = listOf(role))
        }
    }
}
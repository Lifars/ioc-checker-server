package com.lifars.ioc.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.time.Duration
import java.util.*

class JwtProvider(
    secret: String,
    timeout: Duration,
    val issuer: String,
    val audience: String
) {
    private val validityInMs = timeout.toMillis()
    private val algorighm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorighm)
        .withIssuer(issuer)
        .build()

    fun decodeJWT(token: String): DecodedJWT = JWT.require(algorighm).build().verify(token)

    fun createJWT(email: String): String =
        JWT.create()
            .withIssuedAt(Date())
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorighm)
}
package com.lifars.ioc.server.security

import at.favre.lib.crypto.bcrypt.BCrypt
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies
import com.lifars.ioc.server.config.AppConstants


class PasswordHasher(private val pepper: String) {

//    private val bcryptHasher = BCrypt.with(BCrypt.Version.VERSION_2Y, LongPasswordStrategies.hashSha512(BCrypt.Version.VERSION_2Y))
//    private val bcryptVerifyer = BCrypt.verifyer(BCrypt.Version.VERSION_2Y, LongPasswordStrategies.hashSha512(BCrypt.Version.VERSION_2Y))
private val bcryptHasher = BCrypt.with(BCrypt.Version.VERSION_2Y, LongPasswordStrategies.truncate(BCrypt.Version.VERSION_2Y))
    private val bcryptVerifyer = BCrypt.verifyer(BCrypt.Version.VERSION_2Y, LongPasswordStrategies.truncate(BCrypt.Version.VERSION_2Y))
    private val cost = 13

    fun hash(text: String): ByteArray =
        bcryptHasher.hash(cost, peppered(text))

    fun verify(text: String, hashed: ByteArray): Boolean =
        bcryptVerifyer.verifyStrict(peppered(text), hashed).verified

    private fun peppered(text: String) = (text + pepper).toByteArray(AppConstants.charset)
}
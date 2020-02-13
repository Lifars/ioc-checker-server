package com.lifars.ioc.server.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlin.reflect.KClass

object JsonConverter {
    val implementation = ObjectMapper()
        .registerKotlinModule()
        .also { it.additionalSetup() }

    fun <T> toJson(something: T): String =
        implementation.writeValueAsString(something)

    inline fun <reified T> fromJson(json: String): T =
        implementation.readValue(json, T::class.java)

    fun <T: Any> fromJson(json: String, klass: KClass<T>): T =
        implementation.readValue(json, klass.java)

}

fun ObjectMapper.additionalSetup() {
    registerModule(JavaTimeModule())
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
}

fun <T> T.json(): String = JsonConverter.toJson(this)

inline fun <reified T> String.fromJson(): T = JsonConverter.fromJson(this)
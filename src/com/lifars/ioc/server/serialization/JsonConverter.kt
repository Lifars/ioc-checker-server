package com.lifars.ioc.server.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object JsonConverter {
    val implementation = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    fun <T> toJson(something: T): String =
        implementation.writeValueAsString(something)

    inline fun <reified T> fromJson(json: String): T =
        implementation.readValue(json, T::class.java)

}

fun <T> T.json(): String = JsonConverter.toJson(this)

inline fun <reified T> String.fromJson(): T = JsonConverter.fromJson(this)


//import com.google.gson.GsonBuilder
//import com.google.gson.JsonDeserializationContext
//import com.google.gson.JsonDeserializer
//import com.google.gson.JsonElement
//import java.lang.reflect.Type
//import java.time.Instant
//
//object JsonConverter {
//    val implementation = GsonBuilder()
//        .registerTypeAdapter(Instant::class.java, InstantDeserializer())
//        .create()
//
//    fun <T> toJson(something: T): String =
//        implementation.toJson(something)
//
//    inline fun <reified T> fromJson(json: String): T =
//        implementation.fromJson(json, T::class.java)
//
//}
//
//fun <T> T.json(): String = JsonConverter.toJson(this)
//
//inline fun <reified T> String.fromJson(): T = JsonConverter.fromJson(this)
//
//
//class InstantDeserializer() : JsonDeserializer<Instant>{
//    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Instant? =
//        json?.let { Instant.parse(json.asJsonPrimitive.asString) }
//}

//package com.lifars.ioc.server.serialization
//
//import com.beust.klaxon.JsonObject
//import com.beust.klaxon.Klaxon
//import com.beust.klaxon.KlaxonException
//import java.io.StringReader
//import kotlin.reflect.KClass
//
//class JsonConverter {
//    val implementation = Klaxon()
//
//    fun <T> toJson(something: T): String =
//        implementation.toJsonString(something)
//
//    inline fun <reified T> fromJson(json: String): T =
//        implementation.parse<T>(json) ?: throw KlaxonException("Cannot serialize JSON \'${json.substring(0, 20)}...\' to type ${T::class::simpleName}")
//
//    fun <T> fromJson(json: String, kClass: KClass<*>): T =
//        implementation.parse(json, kClass) ?: throw KlaxonException("Cannot serialize JSON \'${json.substring(0, 20)}...\' to type ${kClass.simpleName}")
//}
//
//val jsonConverter = JsonConverter()
//
//fun <T> T.json(): String = jsonConverter.toJson(this)
//
//inline fun <reified T> String.fromJson(): T = jsonConverter.fromJson(this)
//
//private fun <T> Klaxon.parse(json: String, kClass: KClass<*>): T?
//        = fromJsonObject(parser(kClass).parse(StringReader(json)) as JsonObject, kClass.java, kClass) as T?

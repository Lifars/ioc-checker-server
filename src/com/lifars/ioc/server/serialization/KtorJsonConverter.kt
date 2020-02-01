//package com.lifars.ioc.server.serialization
//
//import com.beust.klaxon.JsonObject
//import com.beust.klaxon.Klaxon
//import io.ktor.application.ApplicationCall
//import io.ktor.application.call
//import io.ktor.features.ContentConverter
//import io.ktor.features.ContentNegotiation
//import io.ktor.features.suitableCharset
//import io.ktor.http.ContentType
//import io.ktor.http.content.TextContent
//import io.ktor.http.withCharset
//import io.ktor.request.ApplicationReceiveRequest
//import io.ktor.request.contentCharset
//import io.ktor.util.KtorExperimentalAPI
//import io.ktor.util.pipeline.PipelineContext
//import io.ktor.utils.io.ByteReadChannel
//import io.ktor.utils.io.jvm.javaio.toInputStream
//import java.io.StringReader
//import kotlin.reflect.KClass
//
//class KtorJsonConverter(): ContentConverter{
//    override suspend fun convertForReceive(
//        context: PipelineContext<ApplicationReceiveRequest,
//                ApplicationCall>): Any? {
//        val request = context.subject
//        val channel = request.value as? ByteReadChannel ?: return null
//        val reader = channel.toInputStream().reader(context.call.request.contentCharset() ?: Charsets.UTF_8)
//        val type = request.type
//
//        return jsonConverter.fromJson(reader.use { it.readText() }, type)
//    }
//
//    @KtorExperimentalAPI
//    override suspend fun convertForSend(
//        context: PipelineContext<Any, ApplicationCall>,
//        contentType: ContentType,
//        value: Any
//    ): Any? {
//        return TextContent(value.json(), contentType.withCharset(context.call.suitableCharset()))
//    }
//
//}
//
//fun ContentNegotiation.Configuration.json(
//    contentType: ContentType = ContentType.Application.Json
//) {
//    val converter = KtorJsonConverter()
//    register(contentType, converter)
//}
//
//private fun <T> Klaxon.parse(json: String, kClass: KClass<*>): T?
//        = fromJsonObject(parser(kClass).parse(StringReader(json)) as JsonObject, kClass.java, kClass) as T?

package com.lifars.ioc.server.routes

import com.lifars.ioc.server.config.ADMIN_AUTH
import com.lifars.ioc.server.config.UserPrincipal
import com.lifars.ioc.server.exceptions.AuthorizationException
import com.lifars.ioc.server.payload.*
import com.lifars.ioc.server.routes.locations.AdminLocations
import com.lifars.ioc.server.serialization.fromJson
import com.lifars.ioc.server.service.*
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpHeaders
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.util.pipeline.PipelineContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

private fun PipelineContext<Unit, ApplicationCall>.contentRangeHeader(
    pagination: Payload.Request.Pagination,
    totalCount: Int
) {
    val fromRow = pagination.let { (it.page - 1) * it.perPage }
    val toRow = pagination.let { fromRow + it.perPage }
    call.response.header(HttpHeaders.ContentRange, "probes $fromRow-$toRow/$totalCount")
}

@KtorExperimentalLocationsAPI
fun Route.adminProbe(
    service: ProbeService
) {

    authenticate(ADMIN_AUTH) {
        get<AdminLocations.List.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.One.Probe> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.Many.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.ManyReference.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<AdminLocations.Create.Probe> {
            val request = call.receive<ProbePayload.Request.Create>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        put<AdminLocations.Update.Probe> {
            val request = call.receive<ProbePayload.Request.Update>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        put<AdminLocations.UpdateMany.Probe> {
            val request = call.receive<ProbePayload.Request.UpdateMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        delete<AdminLocations.Delete.Probe> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<AdminLocations.DeleteMany.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.adminIoc(
    service: IocService
) {

    authenticate(ADMIN_AUTH) {

        get<AdminLocations.List.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.One.Ioc> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.Many.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.ManyReference.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<AdminLocations.Create.Ioc> {
            val request = call.receive<IocPayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.Update.Ioc> {
            val request = call.receive<IocPayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.UpdateMany.Ioc> {
            val request = call.receive<IocPayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<AdminLocations.Delete.Ioc> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<AdminLocations.DeleteMany.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.adminProbeReport(
    service: ProbeReportService
) {

    authenticate(ADMIN_AUTH) {
        get<AdminLocations.List.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.One.ProbeReport> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.Many.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.ManyReference.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<AdminLocations.Create.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.Update.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.UpdateMany.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<AdminLocations.Delete.ProbeReport> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<AdminLocations.DeleteMany.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.adminUser(
    service: UserService
) {
    authenticate(ADMIN_AUTH) {
        get<AdminLocations.List.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.One.User> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.Many.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.ManyReference.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<AdminLocations.Create.User> {
            val request = call.receive<UserPayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.Update.User> {
            val request = call.receive<UserPayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.UpdateMany.User> {
            val request = call.receive<UserPayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<AdminLocations.Delete.User> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<AdminLocations.DeleteMany.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.adminFeedSource(
    service: FeedSourceService
) {
    authenticate(ADMIN_AUTH) {
        get<AdminLocations.List.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.One.FeedSource> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.Many.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<AdminLocations.ManyReference.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<AdminLocations.Create.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.Update.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<AdminLocations.UpdateMany.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<AdminLocations.Delete.FeedSource> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<AdminLocations.DeleteMany.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}
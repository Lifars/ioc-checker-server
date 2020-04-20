package com.lifars.ioc.server.routes

import com.lifars.ioc.server.config.ADMIN_AUTH
import com.lifars.ioc.server.config.USER_AUTH
import com.lifars.ioc.server.config.UserPrincipal
import com.lifars.ioc.server.exceptions.AuthorizationException
import com.lifars.ioc.server.payload.*
import com.lifars.ioc.server.routes.locations.FrontEndLocations
import com.lifars.ioc.server.serialization.fromJson
import com.lifars.ioc.server.service.*
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

@KtorExperimentalLocationsAPI
fun Route.probeFrontEnd(
    service: ProbeService
) {
    authenticate(ADMIN_AUTH) {
        get<FrontEndLocations.List.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.One.Probe> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<FrontEndLocations.Create.Probe> {
            val request = call.receive<ProbePayload.Request.Create>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        put<FrontEndLocations.Update.Probe> {
            val request = call.receive<ProbePayload.Request.Update>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update probes")
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.Probe> {
            val request = call.receive<ProbePayload.Request.UpdateMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update probes")
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.Probe> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }

    authenticate(USER_AUTH) {
        get<FrontEndLocations.List.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            val response = service.find(request, principal.id)
            call.respond(response)
        }

        get<FrontEndLocations.One.Probe> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            val response = service.find(request, principal.id)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            val response = service.find(request, principal.id)
            call.respond(response)
        }

        post<FrontEndLocations.Create.Probe> {
            throw AuthorizationException("User is not authorized to create probes")
        }

        put<FrontEndLocations.Update.Probe> {
            throw AuthorizationException("User is not authorized to update probes")
        }

        put<FrontEndLocations.UpdateMany.Probe> {
            throw AuthorizationException("User is not authorized to update probes")
        }

        delete<FrontEndLocations.Delete.Probe> {
            throw AuthorizationException("User is not authorized to delete probes")
        }

        delete<FrontEndLocations.DeleteMany.Probe> {
            throw AuthorizationException("User is not authorized to delete probes")
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.adminIoc(
    service: IocService
) {

    authenticate(ADMIN_AUTH) {

        get<FrontEndLocations.List.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.One.Ioc> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<FrontEndLocations.Create.Ioc> {
            val request = call.receive<IocPayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.Ioc> {
            val request = call.receive<IocPayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.Ioc> {
            val request = call.receive<IocPayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.Ioc> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.adminFoundIoc(
    service: FoundIocService
) {

    authenticate(ADMIN_AUTH) {

        get<FrontEndLocations.List.FoundIoc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.One.FoundIoc> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.FoundIoc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.FoundIoc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<FrontEndLocations.Create.FoundIoc> {
            val request = call.receive<FoundIocPayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.FoundIoc> {
            val request = call.receive<FoundIocPayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.FoundIoc> {
            val request = call.receive<FoundIocPayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.FoundIoc> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.FoundIoc> { parameters ->
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
        get<FrontEndLocations.List.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.One.ProbeReport> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<FrontEndLocations.Create.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.ProbeReport> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.ProbeReport> { parameters ->
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
        get<FrontEndLocations.List.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.One.User> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<FrontEndLocations.Create.User> {
            val request = call.receive<UserPayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.User> {
            val request = call.receive<UserPayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.User> {
            val request = call.receive<UserPayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.User> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.User> { parameters ->
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
        get<FrontEndLocations.List.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.One.FeedSource> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val response = service.find(request)
            call.respond(response)
        }

        post<FrontEndLocations.Create.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.Create>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.Update>()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.UpdateMany>()
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.FeedSource> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}
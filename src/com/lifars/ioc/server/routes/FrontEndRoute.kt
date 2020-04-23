package com.lifars.ioc.server.routes

import com.lifars.ioc.server.config.FRONTEND_AUTH
import com.lifars.ioc.server.config.UserPrincipal
import com.lifars.ioc.server.exceptions.AuthorizationException
import com.lifars.ioc.server.payload.*
import com.lifars.ioc.server.routes.locations.FrontEndLocations
import com.lifars.ioc.server.serialization.fromJson
import com.lifars.ioc.server.service.*
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.util.pipeline.PipelineContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

@KtorExperimentalLocationsAPI
fun Route.probeFrontEnd(
    service: ProbeService
) {
    authenticate(FRONTEND_AUTH) {
        get<FrontEndLocations.List.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probes")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.One.Probe> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probes")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.Many.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probes")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probes")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        post<FrontEndLocations.Create.Probe> {
            val request = call.receive<ProbePayload.Request.Create>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probes")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to create probes")
            }
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        put<FrontEndLocations.Update.Probe> {
            val request = call.receive<ProbePayload.Request.Update>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update probes")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update probes")
            }
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.Probe> {
            val request = call.receive<ProbePayload.Request.UpdateMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update probes")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update probes")
            }
            val response = service.save(request.copy(data = request.data.copy(registeredBy = principal.id)))
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.Probe> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete probes")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete probes")
            }
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.Probe> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete probes")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete probes")
            }
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.frontEndIoc(
    service: IocService
) {
    authenticate(FRONTEND_AUTH) {
        get<FrontEndLocations.List.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.One.Ioc> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.Many.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        post<FrontEndLocations.Create.Ioc> {
            val request = call.receive<IocPayload.Request.Create>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to create IOCs")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.Ioc> {
            val request = call.receive<IocPayload.Request.Update>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update IOCs")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.Ioc> {
            val request = call.receive<IocPayload.Request.UpdateMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update IOCs")
            }
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.Ioc> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete IOCs")
            }
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.Ioc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete IOCs")
            }
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.frontEndFoundIoc(
    service: FoundIocService
) {
    authenticate(FRONTEND_AUTH) {
        get<FrontEndLocations.List.FoundIoc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list found IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.One.FoundIoc> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list found IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.Many.FoundIoc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list found IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.FoundIoc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list found IOCs")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        post<FrontEndLocations.Create.FoundIoc> {
            val request = call.receive<FoundIocPayload.Request.Create>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create found IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to create found IOCs")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.FoundIoc> {
            val request = call.receive<FoundIocPayload.Request.Update>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update found IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update found IOCs")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.FoundIoc> {
            val request = call.receive<FoundIocPayload.Request.UpdateMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update found IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update found IOCs")
            }
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.FoundIoc> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete found IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete found IOCs")
            }
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.FoundIoc> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete found IOCs")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete found IOCs")
            }
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.frontEndProbeReport(
    service: ProbeReportService
) {
    authenticate(FRONTEND_AUTH) {
        get<FrontEndLocations.List.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probe reports")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.One.ProbeReport> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probe reports")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.Many.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probe reports")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list probe reports")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                service.find(request, principal.id)
            }
            call.respond(response)
        }

        post<FrontEndLocations.Create.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.Create>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create probe reports")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to create probe reports")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.Update>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update probe reports")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update probe reports")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.ProbeReport> {
            val request = call.receive<ProbeReportPayload.Request.UpdateMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update probe reports")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to update probe reports")
            }
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.ProbeReport> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete probe reports")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete probe reports")
            }
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.ProbeReport> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete probe reports")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete probe reports")
            }
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.frontEndUser(
    service: UserService
) {
    authenticate(FRONTEND_AUTH) {
        get<FrontEndLocations.List.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list user")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                val requestSelf = Payload.Request.GetOne(principal.id)
                val responseOne = service.find(requestSelf)
                val users = responseOne.data?.let { listOf(it) } ?: emptyList();
                Payload.Response.GetList(data = users, total = users.size)
            }
            call.respond(response)
        }

        get<FrontEndLocations.One.User> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to view user")
            val response = when {
                UserPrincipal.Role.ADMIN in principal.roles -> {
                    service.find(request)
                }
                principal.id == parameters.id -> {
                    service.find(request)
                }
                else -> throw AuthorizationException("User is not authorized to view user")
            }
            call.respond(response)
        }

        get<FrontEndLocations.Many.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list user")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                val requestSelf = Payload.Request.GetOne(principal.id)
                val responseOne = service.find(requestSelf)
                val users = responseOne.data?.let { listOf(it) } ?: emptyList();
                Payload.Response.GetMany(data = users)
            }
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to list user")
            val response = if (UserPrincipal.Role.ADMIN in principal.roles) {
                service.find(request)
            } else {
                val requestSelf = Payload.Request.GetOne(principal.id)
                val responseOne = service.find(requestSelf)
                val users = responseOne.data?.let { listOf(it) } ?: emptyList();
                Payload.Response.GetManyReference(data = users, total = users.size)
            }
            call.respond(response)
        }

        post<FrontEndLocations.Create.User> {
            val request = call.receive<UserPayload.Request.Create>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create users")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to create users")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.User> {
            val request = call.receive<UserPayload.Request.Update>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to create users")
            if (UserPrincipal.Role.ADMIN !in principal.roles && principal.id != request.id) {
                throw AuthorizationException("User is not authorized to update users")
            }
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.User> {
            val request = call.receive<UserPayload.Request.UpdateMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to update users")
            if (UserPrincipal.Role.ADMIN !in principal.roles && listOf(principal.id) != request.ids) {
                throw AuthorizationException("User is not authorized to update users")
            }
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.User> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete users")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete users")
            }

            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.User> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            val principal = call.principal<UserPrincipal>()
                ?: throw AuthorizationException("User is not authorized to delete users")
            if (UserPrincipal.Role.ADMIN !in principal.roles) {
                throw AuthorizationException("User is not authorized to delete users")
            }
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.frontEndFeedSource(
    service: FeedSourceService
) {
    authenticate(FRONTEND_AUTH) {
        get<FrontEndLocations.List.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetList>()
            checkAccessToFeedSources()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.One.FeedSource> { parameters ->
            val request = Payload.Request.GetOne(parameters.id)
            checkAccessToFeedSources()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.Many.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetMany>()
            checkAccessToFeedSources()
            val response = service.find(request)
            call.respond(response)
        }

        get<FrontEndLocations.ManyReference.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.GetManyReference>()
            checkAccessToFeedSources()
            val response = service.find(request)
            call.respond(response)
        }

        post<FrontEndLocations.Create.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.Create>()
            checkAccessToFeedSources()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.Update.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.Update>()
            checkAccessToFeedSources()
            val response = service.save(request)
            call.respond(response)
        }

        put<FrontEndLocations.UpdateMany.FeedSource> {
            val request = call.receive<FeedSourcePayload.Request.UpdateMany>()
            checkAccessToFeedSources()
            val response = service.save(request)
            call.respond(response)
        }

        delete<FrontEndLocations.Delete.FeedSource> { parameters ->
            val request = Payload.Request.Delete(parameters.id)
            checkAccessToFeedSources()
            val response = service.delete(request)
            call.respond(response)
        }

        delete<FrontEndLocations.DeleteMany.FeedSource> { parameters ->
            val request = parameters.query.fromJson<Payload.Request.DeleteMany>()
            checkAccessToFeedSources()
            val response = service.delete(request)
            call.respond(response)
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.checkAccessToFeedSources() {
    val principal = call.principal<UserPrincipal>()
        ?: throw AuthorizationException("User is not authorized to access feed sources")
    if (UserPrincipal.Role.ADMIN !in principal.roles) {
        throw AuthorizationException("User is not authorized to access feed sources")
    }
}
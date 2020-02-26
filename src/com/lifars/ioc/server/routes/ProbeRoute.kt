package com.lifars.ioc.server.routes

import com.lifars.ioc.server.config.PROBE_AUTH
import com.lifars.ioc.server.config.ProbePrincipal
import com.lifars.ioc.server.exceptions.AuthenticationException
import com.lifars.ioc.server.payload.ProbeReportPayload
import com.lifars.ioc.server.routes.locations.ProbeLocations
import com.lifars.ioc.server.service.IocService
import com.lifars.ioc.server.service.ProbeReportService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

@KtorExperimentalLocationsAPI
fun Route.probeIocSearch(
    probeService: ProbeReportService,
    iocService: IocService
){
    authenticate(PROBE_AUTH){
        get<ProbeLocations.Api.Auth.GetIocs> { requestParameters ->
            val principal: ProbePrincipal? = call.principal()
            val page = requestParameters.page
            val result = principal?.let {
                iocService.iocsForProbe(page)
            }?: throw AuthenticationException("Unauthenticated entity tried to get IOCs.")
            logger.info { "Probe with id=${principal.probeId}: authenticated request for IOC definitions on page $page." }
            call.respond(result)
        }

        post<ProbeLocations.Api.Auth.PostIocResult>{
            val principal: ProbePrincipal? = call.principal()
            val request = call.receive<ProbeReportPayload.Request.ReportUpload>()
            principal?.let {
                probeService.saveReport(request, principal)
            }?: throw AuthenticationException("Unauthenticated entity tried to upload IOC request.")
            call.respond(HttpStatusCode.OK, "Report uploaded")
        }
    }
}

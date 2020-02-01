package com.lifars.ioc.server.service

import com.lifars.ioc.server.config.ProbePrincipal
import com.lifars.ioc.server.database.entities.IocSearchError
import com.lifars.ioc.server.database.entities.IocSearchResult
import com.lifars.ioc.server.database.entities.ProbeReport
import com.lifars.ioc.server.database.repository.CrudRepository
import com.lifars.ioc.server.database.repository.ProbeReportRepository
import com.lifars.ioc.server.database.repository.ProbeRepository
import com.lifars.ioc.server.payload.ProbeReportPayload
import java.time.Instant

class ProbeReportService(
    private val probeRepository: ProbeRepository,
    private val probeReportRepository: ProbeReportRepository
) : CrudService<ProbeReport, ProbeReportPayload.ProbeReport, ProbeReportPayload.SaveProbeReport> {

    override val repository: CrudRepository<ProbeReport>
        get() = probeReportRepository

    override fun ProbeReport.toDto() = ProbeReportPayload.ProbeReport(
        id = this.id,
        created = this.created,
        updated = this.updated,
        iocResults = this.iocResults.map { it.toDto() },
        iocErrors = this.iocErrors.map { it.toDto() },
        probeId = this.probeId,
        probeTimestamp = this.probeTimestamp,
        foundIocs = this.foundIocs
    )

    private fun IocSearchResult.toDto() = ProbeReportPayload.IocSearchResult(
        iocId = iocId,
        data = data
    )

    private fun IocSearchError.toDto() = ProbeReportPayload.IocSearchError(
        iocId = iocId,
        kind = kind,
        message = message
    )

    private fun ProbeReportPayload.IocSearchResult.toEntity(probeReportId: Long) = IocSearchResult(
        iocId = iocId,
        data = data,
        probeReportId = probeReportId
    )

    private fun ProbeReportPayload.IocSearchError.toEntity(probeReportId: Long) = IocSearchError(
        iocId = iocId,
        kind = kind,
        message = message,
        probeReportId = probeReportId
    )

    override fun ProbeReport.toSavedDto() = ProbeReportPayload.SaveProbeReport(
        id = this.id,
        iocResults = this.iocResults.map { it.toDto() },
        iocErrors = this.iocErrors.map { it.toDto() },
        probeId = this.probeId,
        foundIocs = this.foundIocs,
        probeTimestamp = this.probeTimestamp
    )

    override fun ProbeReportPayload.SaveProbeReport.toSaveEntity() = ProbeReport(
        id = this.id,
        iocResults = this.iocResults.map { it.toEntity(this.id) },
        iocErrors = this.iocErrors.map { it.toEntity(this.id) },
        probeId = this.probeId,
        foundIocs = this.foundIocs,
        probeTimestamp = this.probeTimestamp,
        updated = Instant.now(),
        created = Instant.now()
    )


    suspend fun containsByNameAndApiKey(name: String, apiKey: ByteArray): Boolean =
        probeRepository.containsByNameAndApiKey(name, apiKey)

    suspend fun saveReport(
        request: ProbeReportPayload.Request.ReportUpload,
        probePrincipal: ProbePrincipal
    ) {
        val probeReport = ProbeReport(
            id = 0,
            probeTimestamp = request.datetime,
            created = Instant.now(),
            updated = Instant.now(),
            probeId = probePrincipal.probeId,
            iocResults = request.iocResults.map { iocResult ->
                IocSearchResult(
                    iocId = iocResult.iocId,
                    data = iocResult.data,
                    probeReportId = 0
                )
            },
            iocErrors = request.iocErrors.map { iocError ->
                IocSearchError(
                    iocId = iocError.iocId,
                    probeReportId = 0,
                    message = iocError.message,
                    kind = iocError.kind
                )
            },
            foundIocs = request.foundIocs
        )
        probeReportRepository.create(probeReport)
    }

}
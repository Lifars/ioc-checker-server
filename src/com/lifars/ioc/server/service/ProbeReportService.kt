package com.lifars.ioc.server.service

import com.lifars.ioc.server.config.ProbePrincipal
import com.lifars.ioc.server.database.entities.ProbeReport
import com.lifars.ioc.server.database.repository.ProbeReportRepository
import com.lifars.ioc.server.database.repository.ProbeRepository
import com.lifars.ioc.server.payload.ProbeReportPayload
import java.time.Instant

class ProbeReportService(
    private val probeRepository: ProbeRepository,
    override val repository: ProbeReportRepository
) : CrudService<ProbeReport, ProbeReportPayload.ProbeReport, ProbeReportPayload.SaveProbeReport> {

    override fun ProbeReport.toDto() = ProbeReportPayload.ProbeReport(
        id = this.id,
        created = this.created,
        updated = this.updated,
        probeId = this.probeId,
        probeTimestamp = this.probeTimestamp,
        foundIocsCount = this.foundIocs.size
    )

    override fun ProbeReport.toSavedDto() = ProbeReportPayload.SaveProbeReport(
        id = this.id,
        probeId = this.probeId,
        foundIocs = this.foundIocs,
        probeTimestamp = this.probeTimestamp
    )

    override fun ProbeReportPayload.SaveProbeReport.toSaveEntity() = ProbeReport(
        id = this.id,
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
            foundIocs = request.foundIocs
        )
        repository.create(probeReport)
    }

}
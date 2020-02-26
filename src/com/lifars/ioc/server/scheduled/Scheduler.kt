package com.lifars.ioc.server.scheduled

import mu.KotlinLogging
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.timerTask

data class TaskSettings(
    val start: LocalTime,
    val every: Long,
    val interval: ChronoUnit,
    val name: String = ""
)

object Scheduler {
    private val logger = KotlinLogging.logger { }
    private val timer = Timer(true)

    fun schedule(
        settings: TaskSettings,
        task: () -> Unit
    ) {
        timer.scheduleAtFixedRate(
            timerTask {
                logger.info { "Starting task ${settings.name} at ${ZonedDateTime.now()}" }
                task()
            },
            Date.from(
                settings.start.plusSeconds(120)
                    .atDate(LocalDate.now())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            ),
            Duration.of(settings.every, settings.interval).toMillis()
        )
    }
}
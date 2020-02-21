package com.lifars.ioc.server.config

import com.lifars.ioc.server.scheduled.Scheduler
import com.lifars.ioc.server.scheduled.TaskSettings
import com.lifars.ioc.server.service.FeedService
import io.ktor.application.Application
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.koin.ktor.ext.get
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@KtorExperimentalAPI
fun Application.installScheduledTasks() {
    val feedService = get<FeedService>()
    Scheduler.schedule(
        TaskSettings(
            start = LocalTime.now(),
            name = "Ioc-feed-harvester-scheduled-task",
            every = environment.config.property("feed.every").getString().toLong(),
            interval = ChronoUnit.HOURS
        )
    ) { runBlocking { feedService.harvestFeeds() } }
}
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
                settings.start.plusSeconds(10)
                    .atDate(LocalDate.now())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            ),
            Duration.of(settings.every, settings.interval).toMillis()
        )
    }
}

//object ScheduledTasks {
//    private val scheduler = StdSchedulerFactory().scheduler
//
//    init {
//        scheduler.start()
//    }
//
//    fun schedule(
//        settings: TaskSettings,
//        task: () -> Unit
//    ) {
//        val taskWrapped = Job { task() }
//        val job =  newJob(taskWrapped.javaClass)
//            .build()
//
//        val trigger = newTrigger()
//            .startAt(Date.from(settings.start.atZone(ZoneId.systemDefault()).toInstant()))
//            .e
//
//    }
//}
package com.example.plugins

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.impl.StdSchedulerFactory

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class MyJob : Job {
    override fun execute(context: JobExecutionContext?) {
        println("Tarea ejecutada cada 5 segundos")
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/test") {
            // Crear el Job que se ejecutará cada 5 segundos
            val job = JobBuilder.newJob(MyJob::class.java)
                .withIdentity("myJob", "group1")
                .build()

            // Crear el Trigger que define la frecuencia (cada 5 segundos)
            val trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()  // Empezar de inmediato
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(5)  // Ejecutar cada 5 segundos
                    .repeatForever())  // Repetir indefinidamente
                .build()

            // Crear y configurar el Scheduler
            val scheduler = StdSchedulerFactory.getDefaultScheduler()
            scheduler.start()

            // Programar el Job con el Trigger
            scheduler.scheduleJob(job, trigger)

            // Responder que el Quartz Job ha sido iniciado
            call.respondText("Quartz job iniciado. Ejecutándose cada 5 segundos.")
        }
    }
}

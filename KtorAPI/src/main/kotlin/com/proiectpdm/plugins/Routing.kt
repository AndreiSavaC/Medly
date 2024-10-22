package com.proiectpdm.plugins

import com.proiectpdm.db.DoctorService
import com.proiectpdm.db.PacientService
import com.proiectpdm.routes.doctorRoute
import com.proiectpdm.routes.pacientRoute

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(doctorService: DoctorService=get(),pacientService: PacientService =get()) {
    routing {

        doctorRoute(doctorService)
        pacientRoute(pacientService)
    }
}
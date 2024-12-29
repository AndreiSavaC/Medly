package com.proiectpdm.plugins

import com.proiectpdm.db.AppointmentService
import com.proiectpdm.db.CategoriesSymptomsService
import com.proiectpdm.db.DoctorService
import com.proiectpdm.db.PacientService
import com.proiectpdm.routes.CategoriesSymptomsRoutes

import com.proiectpdm.routes.appointmentRoute
import com.proiectpdm.routes.doctorRoute
import com.proiectpdm.routes.pacientRoute

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(doctorService: DoctorService=get(),pacientService: PacientService =get(),appointmentService: AppointmentService =get(),categoriesSymptomsService: CategoriesSymptomsService =get()) {

    routing {

        doctorRoute(doctorService)
        pacientRoute(pacientService)
        appointmentRoute(appointmentService,pacientService,doctorService)
        CategoriesSymptomsRoutes(categoriesSymptomsService)

    }
}
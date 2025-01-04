package com.proiectpdm.plugins

import com.proiectpdm.routers.appointments.appointmentRoutes
import com.proiectpdm.routers.diseaseCategories.diseaseCategoriesRoutes
import com.proiectpdm.routers.diseaseSymptoms.diseaseSymptomsRoutes
import com.proiectpdm.routers.insurance.fictionalInsuredRoutes
import com.proiectpdm.routers.user.userRoutes
import com.proiectpdm.services.appointment.AppointmentService
import com.proiectpdm.services.diseaseCategories.DiseaseCategoriesService
import com.proiectpdm.services.diseaseSymptoms.DiseaseSymptomsService
import com.proiectpdm.services.insurance.InsuranceService
import com.proiectpdm.services.user.UserService

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    userService: UserService = get(),
    appointmentService: AppointmentService = get(),
    diseaseCategoriesService: DiseaseCategoriesService = get(),
    diseaseSymptomsService: DiseaseSymptomsService = get(),
    insuranceService: InsuranceService = get(),
) {

    routing {
        userRoutes(userService)
        appointmentRoutes(appointmentService)
        diseaseCategoriesRoutes(diseaseCategoriesService)
        diseaseSymptomsRoutes(diseaseSymptomsService)
        fictionalInsuredRoutes(insuranceService)
    }
}
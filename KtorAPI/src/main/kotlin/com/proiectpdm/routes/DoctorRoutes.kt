package com.proiectpdm.routes

import com.proiectpdm.db.DoctorService
import com.proiectpdm.model.Doctor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.doctorRoute(doctorService: DoctorService) {
    route("/doctors") {
        get {
            val doctors = doctorService.getAllDoctors()
            call.respond(HttpStatusCode.OK, doctors)
        }

        post {
            val doctor = call.receive<Doctor>()
            doctorService.addDoctor(doctor)?.let {
                call.respond(HttpStatusCode.Created, it)
            } ?: call.respond(HttpStatusCode.BadRequest, "Error while adding doctor!")
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                doctorService.getDoctorById(id)?.let {
                    call.respond(HttpStatusCode.OK, it)
                } ?: call.respond(HttpStatusCode.NotFound, "Doctor not found!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                val updatedDoctor = call.receive<Doctor>().copy(id = id)
                doctorService.updateDoctor(updatedDoctor)?.let {
                    call.respond(HttpStatusCode.OK, it)
                } ?: call.respond(HttpStatusCode.BadRequest, "Error while updating doctor!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                if (doctorService.deleteDoctor(id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Doctor not found!")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }
    }
}

package com.proiectpdm.routes

import com.proiectpdm.db.PacientService
import com.proiectpdm.model.Pacient
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.pacientRoute(pacientService: PacientService) {
    route("/pacients") {
        get {
            val pacients = pacientService.getAllPacients()
            call.respond(HttpStatusCode.OK, pacients)
        }

        post {
            val pacient = call.receive<Pacient>()
            pacientService.addPacient(pacient)?.let {
                call.respond(HttpStatusCode.Created, it)
            } ?: call.respond(HttpStatusCode.BadRequest, "Error while adding pacient!")
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                pacientService.getPacientById(id)?.let {
                    call.respond(HttpStatusCode.OK, it)
                } ?: call.respond(HttpStatusCode.NotFound, "Pacient not found!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                val updatedPacient = call.receive<Pacient>().copy(id = id)
                pacientService.updatePacient(updatedPacient)?.let {
                    call.respond(HttpStatusCode.OK, "Pacient with ID $id updated successfully.")
                } ?: call.respond(HttpStatusCode.BadRequest, "Error while updating pacient!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                if (pacientService.deletePacient(id)) {
                    call.respond(HttpStatusCode.NoContent, "Pacient with ID $id deleted successfully.")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Pacient not found!")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }
    }
}

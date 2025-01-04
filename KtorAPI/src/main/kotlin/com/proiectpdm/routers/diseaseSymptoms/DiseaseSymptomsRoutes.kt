package com.proiectpdm.routers.diseaseSymptoms

import com.proiectpdm.services.diseaseCategories.DiseaseCategoriesService
import com.proiectpdm.services.diseaseSymptoms.DiseaseSymptomsService
import com.proiectpdm.models.DiseaseSymptom
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*


fun Routing.diseaseSymptomsRoutes(
    diseaseSymptomsService: DiseaseSymptomsService
) {
    route("/symptoms") {
        get {
            val symptoms = diseaseSymptomsService.getSymptoms()
            if (symptoms.isNotEmpty()) call.respond(HttpStatusCode.OK, symptoms)
            call.respond(HttpStatusCode.BadRequest, "No symptoms found")

        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val symptom = diseaseSymptomsService.getSymptomById(id)
            if (symptom != null) call.respond(HttpStatusCode.OK, symptom)
            call.respond(HttpStatusCode.NotFound, "No symptom found for id $id")
        }

        get("/category/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val symptoms = diseaseSymptomsService.getSymptomsByCategory(id)
            if (symptoms.isNotEmpty()) call.respond(HttpStatusCode.OK, symptoms)
            call.respond(HttpStatusCode.NotFound, "No symptoms found")
        }
    }

    post {
        val newSymptom = call.receive<DiseaseSymptom>()
        diseaseSymptomsService.addSymptom(newSymptom)?.let {
            call.respond(HttpStatusCode.OK, it)
        } ?: call.respond(HttpStatusCode.BadRequest, "Error while adding new disease symptom")
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toInt()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@put
        }
        val updatedSymptom = call.receive<DiseaseSymptom>()
        val wasUpdated = diseaseSymptomsService.updateSymptom(id, updatedSymptom)
        if (wasUpdated) call.respond(HttpStatusCode.OK, "Disease symptom record updated successfully")
        call.respond(HttpStatusCode.NotFound, "No disease symptom found with ID $id")
    }

    delete("/{id}") {
        val id = call.parameters["id"]?.toInt()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
            return@delete
        }
        val wasDeleted = diseaseSymptomsService.deleteSymptom(id)
        if (wasDeleted) call.respond(HttpStatusCode.OK, "Disease symptom record deleted successfully")
        call.respond(HttpStatusCode.NotFound, "No disease symptom found with ID $id")
    }
}

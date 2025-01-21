package com.proiectpdm.routers.insurance

import com.proiectpdm.models.Insurance
import com.proiectpdm.services.insurance.InsuranceService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.fictionalInsuredRoutes(insuranceService: InsuranceService) {
    route("/insurances") {

        get {
            val insurances = insuranceService.getInsurances()
            if (insurances.isNotEmpty()) call.respond(HttpStatusCode.OK, insurances)
            call.respond(HttpStatusCode.NotFound, "No insurances found")
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@get
            }
            val insurance = insuranceService.getInsuranceById(id)
            if (insurance != null) call.respond(insurance)
            call.respond(HttpStatusCode.NotFound, "No insurance found with ID $id")
        }

        get("/code/{code}") {
            val code = call.parameters["code"]?.toDoubleOrNull()
            if (code == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing insurance code")
                return@get
            }
            val insurance = insuranceService.getInsuranceByCode(code)
            if (insurance != null) call.respond(insurance)
            call.respond(HttpStatusCode.NotFound, "No insurance found with ID $code")
        }

        post {
            val newInsurance = call.receive<Insurance>()
            insuranceService.addInsurance(newInsurance)?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(HttpStatusCode.BadRequest, "Error while adding new insurance")
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@put
            }
            val updatedInsurance = call.receive<Insurance>()
            val wasUpdated = insuranceService.updateInsurance(id, updatedInsurance)
            if (wasUpdated) call.respond(HttpStatusCode.OK, "Insured record updated successfully")
            call.respond(HttpStatusCode.NotFound, "No insured found with ID $id")
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@delete
            }
            val wasDeleted = insuranceService.deleteInsurance(id)
            if (wasDeleted) call.respond(HttpStatusCode.OK, "Insured record deleted successfully")
            call.respond(HttpStatusCode.NotFound, "No insured found with ID $id")
        }
    }
}

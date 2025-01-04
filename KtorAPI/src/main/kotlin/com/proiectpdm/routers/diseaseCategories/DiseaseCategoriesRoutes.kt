package com.proiectpdm.routers.diseaseCategories

import com.proiectpdm.services.diseaseCategories.DiseaseCategoriesService
import com.proiectpdm.models.DiseaseCategory
import com.proiectpdm.models.Insurance
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*


fun Routing.diseaseCategoriesRoutes(diseaseCategoriesService: DiseaseCategoriesService) {
    route("/categories") {
        get {
            val categories = diseaseCategoriesService.getCategories()
            if (categories.isNotEmpty()) call.respond(HttpStatusCode.OK, categories)
            call.respond(HttpStatusCode.NotFound, "No disease categories found")
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val category = diseaseCategoriesService.getCategoryById(id)
            if (category != null) call.respond(HttpStatusCode.OK, category)
            call.respond(HttpStatusCode.NotFound, "No disease category found for ID $id")
        }

        post {
            val newDiseaseCategory = call.receive<DiseaseCategory>()
            diseaseCategoriesService.addCategory(newDiseaseCategory)?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: call.respond(HttpStatusCode.BadRequest, "Error while adding new disease category")
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val updatedCategory = call.receive<DiseaseCategory>()
            val wasUpdated = diseaseCategoriesService.updateCategory(id, updatedCategory)
            if (wasUpdated) call.respond(HttpStatusCode.OK, "Disease category record updated successfully")
            call.respond(HttpStatusCode.NotFound, "No disease category found with ID $id")

        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            val wasDeleted = diseaseCategoriesService.deleteCategory(id)
            if (wasDeleted) call.respond(HttpStatusCode.OK, "Disease category record deleted successfully")
            call.respond(HttpStatusCode.NotFound, "No disease category found with ID $id")
        }
    }
}
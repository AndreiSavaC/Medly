package com.proiectpdm.routes

import com.proiectpdm.db.CategoriesSymptomsService
import com.proiectpdm.model.Category
import com.proiectpdm.model.Symptom
import com.proiectpdm.model.Symptoms
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*


fun Routing.CategoriesSymptomsRoutes(categoriesSymptomsService: CategoriesSymptomsService) {
    route("/categories") {
        get{
            val categories = categoriesSymptomsService.getAllCategories()
            call.respond(HttpStatusCode.OK,categories)
        }

        post {
            val cat = call.receive<Category>()
            val existingCategory = categoriesSymptomsService.getAllCategories().find { it.name == cat.name }
            if(existingCategory != null) {
                call.respond(HttpStatusCode.BadRequest, "Category already exists")
            }
            categoriesSymptomsService.addCategory(cat)?.let {
                call.respond(HttpStatusCode.OK,it)
            }?: call.respond(HttpStatusCode.BadRequest,"Error in adding Category")
        }

        delete("/{categoryId}") {
            val categoryId = call.parameters["categoryId"]?.toInt()
            if(categoryId != null){
                if(categoriesSymptomsService.deleteCategory(categoryId))
                {
                    call.respond(HttpStatusCode.NoContent)
                }else{
                    call.respond(HttpStatusCode.NotFound,"Category not found")
                }
            }else{
                call.respond(HttpStatusCode.BadRequest,"Category not found")
            }
        }

        get("/{categoryId}") {
            val categoryId = call.parameters["categoryId"]?.toInt()
            if(categoryId != null){
                categoriesSymptomsService.getCategoryById(categoryId)?.let {
                    call.respond(HttpStatusCode.OK,it)
                }
            }else{
                call.respond(HttpStatusCode.BadRequest, "No category found")
            }
        }

        put("/{categoryId}") {
            val categoryId = call.parameters["categoryId"]?.toInt()
            if(categoryId != null){
                val updatedCategory = call.receive<Category>()

                categoriesSymptomsService.updateCategory(updatedCategory)?.let {
                    call.respond(HttpStatusCode.OK,updatedCategory)
                }?: call.respond(HttpStatusCode.BadRequest,"Error in updating category")
            }else{
                call.respond(HttpStatusCode.NotFound,"Category not found")
            }
        }
    }

    route("/symptoms"){
        post {
            val symptomsParam = call.receive<Symptom>()
            val validCategory = categoriesSymptomsService.getCategoryById(symptomsParam.categoryId) != null
            val existingSymptom = categoriesSymptomsService.getAllSymptoms().find { it.name == symptomsParam.name }

            if(validCategory && existingSymptom == null) {
                categoriesSymptomsService.addSymptom(symptomsParam)?.let {
                    call.respond(HttpStatusCode.OK,it)
                }?: call.respond(HttpStatusCode.BadRequest,"Error in adding symptom")
            }else if(existingSymptom != null){
                call.respond(HttpStatusCode.Conflict, "Duplicate symptom found")
            }
            else{
                call.respond(HttpStatusCode.NotFound,"Error in adding symptom")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if(id != null){
                if(categoriesSymptomsService.deleteSymptom(id)){
                    call.respond(HttpStatusCode.NoContent)
                }else{
                    call.respond(HttpStatusCode.NotFound,"Error in deleting category")
                }
            }else{
                call.respond(HttpStatusCode.NotFound,"No symptom found")
            }
        }

        get {
            val symptoms = categoriesSymptomsService.getAllSymptoms()
            call.respond(HttpStatusCode.OK,symptoms)
        }

        get("/{symptomId}"){
            val symptomId = call.parameters["symptomId"]?.toInt()
            if(symptomId != null){
                categoriesSymptomsService.getSymptomById(symptomId)?.let {
                    call.respond(HttpStatusCode.OK,it)
                }?: call.respond(HttpStatusCode.BadRequest,"Error in get symptom")
            }else{
                call.respond(HttpStatusCode.BadRequest,"No symptom found")
            }
        }

        get("/byCategory/{categoryId}"){
            val categoryId = call.parameters["categoryId"]?.toInt()
            if(categoryId != null){
               val symptoms =  categoriesSymptomsService.getSymptomsByCategory(categoryId)
                call.respond(HttpStatusCode.OK,symptoms)
            }else{
                call.respond(HttpStatusCode.NotFound,"No category found")
            }
        }

        put("/{symptomId}"){
            val symptomId = call.parameters["symptomId"]?.toInt()
            if(symptomId != null){
                val updatedSymptom = call.receive<Symptom>()
                categoriesSymptomsService.updateSymptom(updatedSymptom)?.let {
                    call.respond(HttpStatusCode.OK,updatedSymptom)
                }?: call.respond(HttpStatusCode.BadRequest,"Error in updating symptom")
            }else{
                call.respond(HttpStatusCode.NotFound,"No symptom found")
            }
        }
    }
}
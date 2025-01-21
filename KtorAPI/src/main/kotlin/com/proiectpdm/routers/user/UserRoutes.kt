package com.proiectpdm.routers.user

import com.proiectpdm.models.User
import com.proiectpdm.services.keycloak.KeycloakService
import com.proiectpdm.services.user.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.userRoutes(userService: UserService, keycloakService: KeycloakService) {
    route("/users") {
        get {
            val users = userService.getUsers()
            if (users.isNotEmpty()) call.respond(HttpStatusCode.OK, users)
            call.respond(HttpStatusCode.NotFound, "No users found")
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val user = userService.getUserById(id)
            if (user != null) call.respond(HttpStatusCode.OK, user)
            call.respond(HttpStatusCode.NotFound, "No user found with ID $id")
        }

        get("/keycloak/{id}") {
            val id = call.parameters["id"].toString()
            if (id.isBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val user = userService.getUserByKeycloakId(id)
            if (user != null) call.respond(HttpStatusCode.OK, user)
            call.respond(HttpStatusCode.NotFound, "No user found with Keycloak ID $id")
        }

        post {
            val password = call.request.headers["X-User-Password"]
            if (password.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Password is required!")
                return@post
            }
            val user = call.receive<User>()
            try {
                val keycloakId = keycloakService.createUser(user.email, password)
                println(keycloakId)
                val userWithKeycloakId = user.copy(keycloakId = keycloakId)
                userService.addUser(userWithKeycloakId)?.let {
                    call.respond(HttpStatusCode.Created, it)
                } ?: call.respond(HttpStatusCode.InternalServerError, "Error creating user in the database!")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error creating user: ${e.message}")
            }
        }


        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@put
            }
            val updatedUser = call.receive<User>().copy(id = id)
            val wasUpdated = userService.updateUser(updatedUser)
            if (wasUpdated) call.respond(HttpStatusCode.OK, "User record updated successfully")
            call.respond(HttpStatusCode.NotFound, "No user found with ID $id")
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@delete
            }
            val wasDeleted = userService.deleteUser(id)
            if (wasDeleted) call.respond(HttpStatusCode.OK, "User with ID $id deleted successfully")
            call.respond(HttpStatusCode.NotFound, "No user found with ID $id")

        }

    }
}

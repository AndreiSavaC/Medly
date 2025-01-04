package com.proiectpdm.routers.user

import com.proiectpdm.services.user.UserService
import com.proiectpdm.models.User
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.userRoutes(userService: UserService) {
    route("/users") {
        get {
            val users = userService.getUsers()
            call.respond(HttpStatusCode.OK, users)
        }

        post {
            val password = call.request.queryParameters["password"]
            if (password.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Password is required!")
                return@post
            }

            val user = call.receive<User>()

            userService.signUpUser(user, password)?.let {
                call.respond(HttpStatusCode.Created, it)
            } ?: call.respond(HttpStatusCode.BadRequest, "Error creating user!")
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                userService.getUserById(id)?.let {
                    call.respond(HttpStatusCode.OK, it)
                } ?: call.respond(HttpStatusCode.NotFound, "User not found!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                val updatedUser = call.receive<User>().copy(id = id)
                userService.updateUser(updatedUser)?.let {
                    call.respond(HttpStatusCode.OK, "User with ID $id updated successfully.")
                } ?: call.respond(HttpStatusCode.BadRequest, "Error while updating user!")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toInt()
            if (id != null) {
                if (userService.deleteUser(id)) {
                    call.respond(HttpStatusCode.NoContent, "User with ID $id deleted successfully.")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found!")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Provide a valid ID!")
            }
        }
    }
}

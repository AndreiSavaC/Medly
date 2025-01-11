package com.proiectpdm.services.keycloak

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class KeycloakUserRequest(
    val username: String, val email: String, val enabled: Boolean, val credentials: List<Credential>
)

@Serializable
data class Credential(
    val type: String, val value: String, val temporary: Boolean
)

class KeycloakServiceImpl : KeycloakService {

    override suspend fun createUser(email: String, password: String): String {
        val keycloakBaseUrl = "http://keycloak:8080/admin/realms/HealthyApp"
        val adminToken = getKeycloakAdminToken()
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
        val requestBody = KeycloakUserRequest(
            username = email, email = email, enabled = true, credentials = listOf(
                Credential(
                    type = "password", value = password, temporary = false
                )
            )
        )
        try {
            val response = client.post("$keycloakBaseUrl/users") {
                header("Authorization", "Bearer $adminToken")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            if (response.status != HttpStatusCode.Created) {
                throw Exception("Failed to create user in Keycloak: ${response.bodyAsText()}")
            }
            val locationHeader =
                response.headers["Location"] ?: throw Exception("Location header missing in Keycloak response")
            return locationHeader.substringAfterLast("/")
        } finally {
            client.close()
        }
    }

    private suspend fun getKeycloakAdminToken(): String {
        val keycloakTokenUrl = "http://keycloak:8080/realms/master/protocol/openid-connect/token"
        val clientId = "admin-cli"
        val username = "admin"
        val password = "q1w2e3"
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
        try {
            val response: HttpResponse = client.submitForm(
                url = keycloakTokenUrl, formParameters = Parameters.build {
                    append("client_id", clientId)
                    append("username", username)
                    append("password", password)
                    append("grant_type", "password")
                })
            if (response.status != HttpStatusCode.OK) {
                throw Exception("Failed to obtain admin token from Keycloak: ${response.bodyAsText()}")
            }
            val responseBody = Json.decodeFromString<JsonObject>(response.bodyAsText())
            return responseBody["access_token"]?.jsonPrimitive?.content
                ?: throw Exception("Access token not found in Keycloak response")
        } finally {
            client.close()
        }
    }
}

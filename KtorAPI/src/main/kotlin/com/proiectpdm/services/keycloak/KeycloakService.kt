package com.proiectpdm.services.keycloak

interface KeycloakService {
    suspend fun createUser(email: String, password: String): String
}
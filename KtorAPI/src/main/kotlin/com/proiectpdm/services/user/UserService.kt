package com.proiectpdm.services.user

import com.proiectpdm.models.User

interface UserService {
    suspend fun signUpUser(user: User, password: String): User?
    suspend fun getUsers(): List<User>
    suspend fun getUserById(id: Int): User?
    suspend fun getUserByKeycloakId(keycloakId: String): User?
    suspend fun updateUser(user: User): User?
    suspend fun deleteUser(id: Int): Boolean
}
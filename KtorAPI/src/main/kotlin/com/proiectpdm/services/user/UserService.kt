package com.proiectpdm.services.user

import com.proiectpdm.models.User

interface UserService {
    suspend fun addUser(user: User): User?
    suspend fun getUsers(): List<User>
    suspend fun getUserById(id: Int): User?
    suspend fun getUserByKeycloakId(keycloakId: String): User?
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUser(id: Int): Boolean
}
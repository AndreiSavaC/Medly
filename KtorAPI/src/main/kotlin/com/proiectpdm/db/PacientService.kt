package com.proiectpdm.db

import com.proiectpdm.model.Pacient

interface PacientService {
    suspend fun addPacient(pacient: Pacient): Pacient?
    suspend fun getAllPacients(): List<Pacient>
    suspend fun deletePacient(id: Int): Boolean
    suspend fun getPacientById(id: Int): Pacient?
    suspend fun updatePacient(pacient: Pacient): Pacient?
}
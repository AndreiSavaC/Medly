package com.proiectpdm.services.insurance

import com.proiectpdm.models.Insurance

interface InsuranceService {
    suspend fun getInsurances(): List<Insurance>
    suspend fun getInsuranceById(id: Int): Insurance?
    suspend fun getInsuranceByCode(code: Double): Insurance?
    suspend fun addInsurance(data: Insurance): Insurance?
    suspend fun updateInsurance(id: Int, data: Insurance): Boolean
    suspend fun deleteInsurance(id: Int): Boolean
}
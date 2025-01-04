package com.proiectpdm.services.diseaseSymptoms

import com.proiectpdm.models.DiseaseSymptom

interface DiseaseSymptomsService {
    suspend fun getSymptoms(): List<DiseaseSymptom>
    suspend fun getSymptomById(id: Int): DiseaseSymptom?
    suspend fun getSymptomsByCategory(id: Int): List<DiseaseSymptom>
    suspend fun addSymptom(symptom: DiseaseSymptom): DiseaseSymptom?
    suspend fun updateSymptom(id:Int, symptoms: DiseaseSymptom): Boolean
    suspend fun deleteSymptom(id: Int): Boolean
}
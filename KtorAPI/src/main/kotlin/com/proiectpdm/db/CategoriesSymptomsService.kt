package com.proiectpdm.db

import com.proiectpdm.model.Category
import com.proiectpdm.model.Symptom

interface CategoriesSymptomsService {
    suspend fun addCategory(category: Category) : Category?
    suspend fun deleteCategory(categoryId: Int) : Boolean
    suspend fun getAllCategories() : List<Category>
    suspend fun getCategoryById(categoryId: Int): Category?
    suspend fun updateCategory(category: Category) : Category?

    suspend fun addSymptom(symptom: Symptom) : Symptom?
    suspend fun deleteSymptom(symptomId: Int) : Boolean
    suspend fun getAllSymptoms(): List<Symptom>
    suspend fun getSymptomById(symptomId: Int): Symptom?
    suspend fun getSymptomsByCategory(categoryId: Int) : List<Symptom>
    suspend fun updateSymptom(symptoms: Symptom) : Symptom?
}
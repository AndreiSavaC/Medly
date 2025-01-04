package com.proiectpdm.services.diseaseCategories

import com.proiectpdm.models.DiseaseCategory

interface DiseaseCategoriesService {
    suspend fun getCategories(): List<DiseaseCategory>
    suspend fun getCategoryById(id: Int): DiseaseCategory?
    suspend fun addCategory(category: DiseaseCategory): DiseaseCategory?
    suspend fun updateCategory(id: Int, category: DiseaseCategory): Boolean
    suspend fun deleteCategory(id: Int): Boolean
}
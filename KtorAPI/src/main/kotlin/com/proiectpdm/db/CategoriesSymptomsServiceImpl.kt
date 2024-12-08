package com.proiectpdm.db

import com.proiectpdm.model.*
import com.proiectpdm.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CategoriesSymptomsServiceImpl : CategoriesSymptomsService {
    private fun resultRowToCategory(resultRow: ResultRow): Category {
        return Category(
            name = resultRow[Categories.name],
            id = resultRow[Categories.id],
        )
    }

    private fun resultRowToSymptom(resultRow: ResultRow): Symptom {
        return Symptom(
            name = resultRow[Symptoms.name],
            id = resultRow[Symptoms.id],
            categoryId = resultRow[Symptoms.categoryId]
        )
    }

    override suspend fun addCategory(category: Category): Category? = dbQuery {
        val insterStmt = Categories.insert {
            it[name] = category.name
            //it[id] = category.id
        }
        insterStmt.resultedValues?.singleOrNull()?.let { resultRowToCategory(it) }
    }

    override suspend fun deleteCategory(categoryId: Int) = dbQuery{
        Categories.deleteWhere { id.eq(categoryId)} > 0
    }

    override suspend fun getAllCategories(): List<Category>  = dbQuery{
        Categories.selectAll().map { resultRowToCategory(it) }
    }

    override suspend fun getCategoryById(categoryId: Int): Category? = dbQuery {
        Categories.selectAll().where(Categories.id.eq(categoryId)).map { resultRowToCategory(it) }.singleOrNull()
    }

    override suspend fun updateCategory(category: Category): Category? = dbQuery{
        Categories.update({Categories.id.eq(category.id)}){
            it[id] = category.id
            it[name] = category.name
        }
        getCategoryById(category.id)
    }

    override suspend fun addSymptom(symptom: Symptom): Symptom? = dbQuery{
        val insertStmt = Symptoms.insert {
            it[name] = symptom.name
            it[categoryId] = symptom.categoryId
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToSymptom(it) }
    }

    override suspend fun deleteSymptom(symptomId: Int) : Boolean = dbQuery {
        Symptoms.deleteWhere { Symptoms.id.eq(symptomId) } > 0
    }

    override suspend fun getAllSymptoms(): List<Symptom>  = dbQuery{
        Symptoms.selectAll().map { resultRowToSymptom(it) }
    }

    override suspend fun getSymptomById(symptomId: Int): Symptom? = dbQuery {
        Symptoms.selectAll().where(Symptoms.id.eq(symptomId)).singleOrNull()?.let { resultRowToSymptom(it) }
    }

    override suspend fun getSymptomsByCategory(categoryId: Int): List<Symptom> = dbQuery {
        Symptoms.selectAll().where(Symptoms.categoryId.eq(categoryId)).map { resultRowToSymptom(it) }
    }

    override suspend fun updateSymptom(symptoms: Symptom): Symptom? = dbQuery {
        Symptoms.update ({Symptoms.id.eq(symptoms.id)}){
            it[id] = symptoms.id
            it[name] = symptoms.name
            it[categoryId] = symptoms.categoryId
        }
        getSymptomById(symptoms.id)
    }
}
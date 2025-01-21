package com.proiectpdm.services.diseaseCategories

import com.proiectpdm.models.DiseaseCategoriesTable
import com.proiectpdm.models.DiseaseCategory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DiseaseCategoriesServiceImpl : DiseaseCategoriesService {

    override suspend fun getCategories(): List<DiseaseCategory> = transaction {
        DiseaseCategoriesTable.selectAll().map { resultRowToCategory(it) }
    }

    override suspend fun getCategoryById(id: Int): DiseaseCategory? = transaction {
        DiseaseCategoriesTable.selectAll().where(DiseaseCategoriesTable.id eq id).singleOrNull()
            ?.let { resultRowToCategory(it) }
    }

    override suspend fun addCategory(category: DiseaseCategory): DiseaseCategory? = transaction {
        val insertStmt = DiseaseCategoriesTable.insert {
            it[name] = category.name
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToCategory(it) }
    }

    override suspend fun updateCategory(id: Int, category: DiseaseCategory): Boolean = transaction {
        DiseaseCategoriesTable.update({ DiseaseCategoriesTable.id eq id }) {
            it[name] = category.name
        } > 0
    }

    override suspend fun deleteCategory(id: Int): Boolean = transaction {
        DiseaseCategoriesTable.deleteWhere { DiseaseCategoriesTable.id eq id } > 0
    }


    private fun resultRowToCategory(resultRow: ResultRow): DiseaseCategory {
        return DiseaseCategory(
            name = resultRow[DiseaseCategoriesTable.name],
            id = resultRow[DiseaseCategoriesTable.id],
        )
    }

}
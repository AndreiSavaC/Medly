package com.proiectpdm.services.insurance

import com.proiectpdm.models.Insurance
import com.proiectpdm.models.InsuranceTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class InsuranceServiceImpl : InsuranceService {

    override suspend fun getInsurances(): List<Insurance> = transaction {
        InsuranceTable.selectAll().map { resultRowToInsurance(it) }
    }

    override suspend fun getInsuranceById(id: Int): Insurance? = transaction {
        InsuranceTable.selectAll().where { InsuranceTable.id eq id }.singleOrNull()?.let { resultRowToInsurance(it) }
    }

    override suspend fun getInsuranceByCode(code: Double): Insurance? = transaction {
        InsuranceTable.selectAll().where { InsuranceTable.insuranceCode eq code }.singleOrNull()
            ?.let { resultRowToInsurance(it) }
    }


    override suspend fun addInsurance(data: Insurance): Insurance? = transaction {
        val insertStmt = InsuranceTable.insert {
            it[insuranceCode] = data.insuranceCode
            it[firstName] = data.firstName
            it[lastName] = data.lastName
            it[gender] = data.gender
            it[birthday] = data.birthday
            it[doctorId] = data.doctorId
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToInsurance(it) }
    }


    override suspend fun updateInsurance(id: Int, data: Insurance): Boolean = transaction {
        InsuranceTable.update({ InsuranceTable.id eq id }) {
            it[insuranceCode] = data.insuranceCode
            it[firstName] = data.firstName
            it[lastName] = data.lastName
            it[gender] = data.gender
            it[birthday] = data.birthday
            it[doctorId] = data.doctorId
        } > 0
    }

    override suspend fun deleteInsurance(id: Int): Boolean = transaction {
        InsuranceTable.deleteWhere { InsuranceTable.id eq id } > 0
    }

    private fun resultRowToInsurance(row: ResultRow): Insurance {
        return Insurance(
            id = row[InsuranceTable.id],
            insuranceCode = row[InsuranceTable.insuranceCode],
            firstName = row[InsuranceTable.firstName],
            lastName = row[InsuranceTable.lastName],
            gender = row[InsuranceTable.gender],
            birthday = row[InsuranceTable.birthday],
            doctorId = row[InsuranceTable.doctorId],
        )
    }
}

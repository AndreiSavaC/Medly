package com.proiectpdm.db

import com.proiectpdm.model.Doctors
import com.proiectpdm.model.Doctor
import com.proiectpdm.plugins.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*

class DoctorServiceImpl : DoctorService {

    private fun resultRowToDoctor(resultRow: ResultRow): Doctor {
        return Doctor(
            firstName = resultRow[Doctors.firstName],
            lastName = resultRow[Doctors.lastName],
            email = resultRow[Doctors.email],
            speciality = resultRow[Doctors.speciality],
            id = resultRow[Doctors.id]
        )
    }

    override suspend fun addDoctor(doctor: Doctor): Doctor? = dbQuery {
        val insertStmt = Doctors.insert {
            it[firstName] = doctor.firstName
            it[lastName] = doctor.lastName
            it[email] = doctor.email
            it[speciality] = doctor.speciality
        }

        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToDoctor(it) }
    }
    override suspend fun getAllDoctors(): List<Doctor> = dbQuery {
        Doctors.selectAll().map { resultRowToDoctor(it) }
    }

    override suspend fun deleteDoctor(id: Int): Boolean = dbQuery {
        Doctors.deleteWhere { Doctors.id eq id } > 0
    }

    override suspend fun getDoctorById(id: Int): Doctor? = dbQuery {
        Doctors.selectAll().where { Doctors.id eq id }
            .map { resultRowToDoctor(it) }
            .singleOrNull()
    }
    override suspend fun updateDoctor(doctor: Doctor): Doctor? = dbQuery {
        Doctors.update({ Doctors.id eq doctor.id }) {
            it[firstName] = doctor.firstName
            it[lastName] = doctor.lastName
            it[email] = doctor.email
            it[speciality] = doctor.speciality
        }

        getDoctorById(doctor.id)
    }
}
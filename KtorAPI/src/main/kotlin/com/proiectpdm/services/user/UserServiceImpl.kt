package com.proiectpdm.services.user

import com.proiectpdm.models.User
import com.proiectpdm.models.UsersTable
import com.proiectpdm.models.UsersTable.keycloakId
import com.proiectpdm.plugins.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserServiceImpl : UserService {

    override suspend fun getUsers(): List<User> = dbQuery {
        UsersTable.selectAll().map { resultRowToUser(it) }
    }

    override suspend fun getUserById(id: Int): User? = dbQuery {
        UsersTable.selectAll().where { UsersTable.id eq id }.map { resultRowToUser(it) }.singleOrNull()
    }

    override suspend fun getUserByKeycloakId(keycloakId: String): User? = dbQuery {
        UsersTable.selectAll().where { UsersTable.keycloakId eq keycloakId }.map { resultRowToUser(it) }.singleOrNull()
    }

    override suspend fun updateUser(user: User): User? = dbQuery {
        UsersTable.update({ UsersTable.id eq user.id }) {
            it[keycloakId] = user.keycloakId
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[gender] = user.gender
            it[height] = user.height
            it[weight] = user.weight
            it[birthday] = user.birthday
            it[doctorId] = user.doctorId
            it[isDoctor] = user.isDoctor
            it[isAdmin] = user.isAdmin
        }

        getUserById(user.id)
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        UsersTable.deleteWhere { UsersTable.id eq id } > 0
    }

    override suspend fun signUpUser(user: User, password: String): User? = dbQuery {
        val insertStmt = UsersTable.insert {
            it[keycloakId] = user.keycloakId
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[gender] = user.gender
            it[height] = user.height
            it[weight] = user.weight
            it[birthday] = user.birthday
            it[doctorId] = user.doctorId
            it[isDoctor] = user.isDoctor
            it[isAdmin] = user.isAdmin
        }

        insertStmt.resultedValues?.singleOrNull()?.let { resultRowToUser(it) }
    }

    private fun resultRowToUser(resultRow: ResultRow): User {
        return User(
            id = resultRow[UsersTable.id],
            keycloakId = resultRow[keycloakId],
            firstName = resultRow[UsersTable.firstName],
            lastName = resultRow[UsersTable.lastName],
            email = resultRow[UsersTable.email],
            gender = resultRow[UsersTable.gender],
            height = resultRow[UsersTable.height],
            weight = resultRow[UsersTable.weight],
            birthday = resultRow[UsersTable.birthday],
            doctorId = resultRow[UsersTable.doctorId],
            isDoctor = resultRow[UsersTable.isDoctor],
            isAdmin = resultRow[UsersTable.isAdmin],
        )
    }
}

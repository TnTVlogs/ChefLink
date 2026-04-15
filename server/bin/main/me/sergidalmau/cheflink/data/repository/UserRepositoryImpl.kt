package me.sergidalmau.cheflink.data.repository

import me.sergidalmau.cheflink.data.local.RefreshTokensTable
import me.sergidalmau.cheflink.data.local.UsersTable
import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.domain.repository.UserRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.mindrot.jbcrypt.BCrypt

class UserRepositoryImpl : UserRepository {

    override suspend fun login(username: String, password: String): User? = DatabaseFactory.dbQuery {
        val row = UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .singleOrNull() ?: return@dbQuery null

        val hash = row[UsersTable.passwordHash]
        if (BCrypt.checkpw(password, hash)) {
            User(
                id = row[UsersTable.id],
                username = row[UsersTable.username],
                firstName = row[UsersTable.firstName],
                lastName = row[UsersTable.lastName],
                email = row[UsersTable.email],
                role = UserRole.valueOf(row[UsersTable.role])
            )
        } else {
            null
        }
    }

    override suspend fun register(
        username: String, 
        password: String, 
        firstName: String, 
        lastName: String, 
        email: String, 
        role: UserRole
    ): User = DatabaseFactory.dbQuery {
        val id = "u${System.currentTimeMillis()}"
        val hash = BCrypt.hashpw(password, BCrypt.gensalt())
        
        UsersTable.insert {
            it[UsersTable.id] = id
            it[UsersTable.username] = username
            it[UsersTable.passwordHash] = hash
            it[UsersTable.firstName] = firstName
            it[UsersTable.lastName] = lastName
            it[UsersTable.email] = email
            it[UsersTable.role] = role.name
        }

        User(id, username, email, firstName, lastName, role)
    }

    override suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean = DatabaseFactory.dbQuery {
        val row = UsersTable.selectAll()
            .where { UsersTable.id eq userId }
            .singleOrNull() ?: return@dbQuery false

        val hash = row[UsersTable.passwordHash]
        if (BCrypt.checkpw(oldPassword, hash)) {
            val newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt())
            UsersTable.update({ UsersTable.id eq userId }) {
                it[passwordHash] = newHash
            }
            true
        } else {
            false
        }
    }

    suspend fun saveRefreshToken(userId: String, token: String, expiresAt: Long) = DatabaseFactory.dbQuery {
        RefreshTokensTable.insert {
            it[RefreshTokensTable.userId] = userId
            it[RefreshTokensTable.token] = token
            it[RefreshTokensTable.expiresAt] = expiresAt
        }
        Unit
    }

    suspend fun validateRefreshToken(token: String): String? = DatabaseFactory.dbQuery {
        RefreshTokensTable.selectAll()
            .where { (RefreshTokensTable.token eq token) and (RefreshTokensTable.expiresAt greater System.currentTimeMillis()) }
            .map { it[RefreshTokensTable.userId] }
            .singleOrNull()
    }

    suspend fun revokeRefreshToken(token: String) = DatabaseFactory.dbQuery {
        RefreshTokensTable.deleteWhere { RefreshTokensTable.token eq token }
        Unit
    }
}

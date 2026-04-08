package me.sergidalmau.cheflink.domain.repository

import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.models.UserRole

interface UserRepository {
    suspend fun login(username: String, password: String): User?
    suspend fun register(username: String, password: String, firstName: String, lastName: String, email: String, role: UserRole): User
    suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean
}

package me.sergidalmau.cheflink.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import me.sergidalmau.cheflink.data.remote.ApiClient
import me.sergidalmau.cheflink.domain.models.AuthResponse
import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.domain.repository.UserRepository
import me.sergidalmau.cheflink.domain.util.HashUtils
import me.sergidalmau.cheflink.ui.util.AppSession


class RemoteUserRepository(private val baseUrl: String) : UserRepository {
    private val client = ApiClient.client

    override suspend fun login(username: String, password: String): User? {
        return try {
            val response = client.post("$baseUrl/login") {
                contentType(Json)
                setBody(mapOf("username" to username, "password" to HashUtils.sha256(password)))
            }.body<AuthResponse>()
            
            AppSession.loginUser(response.user, response.accessToken, response.refreshToken)
            response.user
        } catch (_: Exception) {
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
    ): User {
        val response = client.post("$baseUrl/register") {
            contentType(Json)
            setBody(mapOf(
                "username" to username, 
                "password" to HashUtils.sha256(password), 
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "role" to role.name
            ))
        }
        
        if (response.status.value !in 200..299) {
            throw io.ktor.client.plugins.ResponseException(response, "Error del servidor: ${response.status.value}")
        }
        
        return response.body<User>()
    }

    override suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        val response = client.post("$baseUrl/users/$userId/password") {
            contentType(Json)
            setBody(mapOf(
                "oldPassword" to HashUtils.sha256(oldPassword), 
                "newPassword" to HashUtils.sha256(newPassword)
            ))
        }
        return response.status.value in 200..299
    }
}

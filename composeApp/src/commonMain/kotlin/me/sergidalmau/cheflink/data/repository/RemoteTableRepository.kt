package me.sergidalmau.cheflink.data.repository

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.sergidalmau.cheflink.data.remote.ApiClient
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.domain.repository.TableRepository

class RemoteTableRepository(private val baseUrl: String) : TableRepository {
    private val client = ApiClient.client

    override suspend fun getTables(): List<Table> {
        return try {
            client.get("$baseUrl/tables").body<List<Table>>()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun createTable(number: Int, capacity: Int): Table {
        return client.post("$baseUrl/tables") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("number" to number, "capacity" to capacity))
        }.body<Table>()
    }

    override suspend fun updateTable(number: Int, capacity: Int) {
        client.put("$baseUrl/tables/$number") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("capacity" to capacity))
        }
    }

    override suspend fun deleteTable(number: Int) {
        client.delete("$baseUrl/tables/$number")
    }
}

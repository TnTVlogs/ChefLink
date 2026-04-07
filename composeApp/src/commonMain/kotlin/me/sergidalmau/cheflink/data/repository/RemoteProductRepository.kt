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
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.domain.repository.ProductRepository

class RemoteProductRepository(private val baseUrl: String) : ProductRepository {
    private val client = ApiClient.client

    override suspend fun getProducts(): List<Product> {
        return client.get("$baseUrl/products").body()
    }

    override suspend fun createProduct(name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean): Product {
        return client.post("$baseUrl/products") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "name" to name,
                "category" to category.name,
                "price" to price.toString(),
                "description" to (description ?: ""),
                "isAvailable" to isAvailable.toString()
            ))
        }.body()
    }

    override suspend fun updateProduct(id: String, name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean) {
        client.put("$baseUrl/products/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "name" to name,
                "category" to category.name,
                "price" to price.toString(),
                "description" to (description ?: ""),
                "isAvailable" to isAvailable.toString()
            ))
        }
    }

    override suspend fun deleteProduct(id: String) {
        client.delete("$baseUrl/products/$id")
    }
}

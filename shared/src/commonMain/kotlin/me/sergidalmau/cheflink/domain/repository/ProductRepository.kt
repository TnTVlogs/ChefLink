package me.sergidalmau.cheflink.domain.repository

import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun createProduct(name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean = true): Product
    suspend fun updateProduct(id: String, name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean)
    suspend fun deleteProduct(id: String)
}

package me.sergidalmau.cheflink.data.repository

import me.sergidalmau.cheflink.data.local.DatabaseFactory
import me.sergidalmau.cheflink.data.local.ProductsTable
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.domain.repository.ProductRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.deleteWhere

class ProductRepositoryImpl : ProductRepository {
    override suspend fun getProducts(): List<Product> = DatabaseFactory.dbQuery {
        ProductsTable.selectAll().map {
            Product(
                id = it[ProductsTable.id],
                name = it[ProductsTable.name],
                category = ProductCategory.valueOf(it[ProductsTable.category]),
                price = it[ProductsTable.price],
                description = it[ProductsTable.description],
                isAvailable = it[ProductsTable.isAvailable]
            )
        }
    }

    override suspend fun createProduct(name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean): Product = DatabaseFactory.dbQuery {
        val newId = "p${System.currentTimeMillis()}"
        ProductsTable.insert {
            it[id] = newId
            it[ProductsTable.name] = name
            it[ProductsTable.category] = category.name
            it[ProductsTable.price] = price
            it[ProductsTable.description] = description
            it[ProductsTable.isAvailable] = isAvailable
        }
        Product(newId, name, category, price, description, isAvailable)
    }

    override suspend fun updateProduct(id: String, name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean) = DatabaseFactory.dbQuery {
        ProductsTable.update({ ProductsTable.id eq id }) {
            it[ProductsTable.name] = name
            it[ProductsTable.category] = category.name
            it[ProductsTable.price] = price
            it[ProductsTable.description] = description
            it[ProductsTable.isAvailable] = isAvailable
        }
        Unit
    }

    override suspend fun deleteProduct(id: String) = DatabaseFactory.dbQuery {
        ProductsTable.deleteWhere { ProductsTable.id eq id }
        Unit
    }
}

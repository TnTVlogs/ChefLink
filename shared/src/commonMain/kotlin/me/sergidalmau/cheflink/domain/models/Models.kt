package me.sergidalmau.cheflink.domain.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
enum class ProductCategory {
    Primers, Segons, Postres, Begudes, Menus
}

@Serializable
enum class OrderStatus {
    PENDING,
    PREPARING,
    READY,
    ENVIADA,  // Added for compatibility
    SERVIDA,  // Added for compatibility
    CANCELLED
}

@Serializable
enum class TableStatus {
    Lliure, Ocupada
}

@Serializable
enum class UserRole {
    Cambrer, Admin
}

@Serializable
data class Product(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val price: Double,
    val description: String? = null,
    val isAvailable: Boolean = true
)

@Serializable
data class OrderItem(
    val product: Product,
    val quantity: Int,
    val notes: String? = null,
    val priceAtTime: Double = product.price
)

@Serializable
data class Order(
    val id: String,
    val tableNumber: Int,
    val items: List<OrderItem>,
    val notes: String? = null,
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val waiterName: String,
    val isSynced: Boolean = true
)

@Serializable
data class Table(
    val number: Int,
    val capacity: Int,
    val status: TableStatus
)

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)

object MockData {
    val products = listOf(
        Product("p1", "Patates Braves", ProductCategory.Primers, 6.5, "Salsa casolana picant"),
        Product("p2", "Amanida César", ProductCategory.Primers, 9.0, "Amb pollastre i crostons"),
        Product("p3", "Hamburguesa Completa", ProductCategory.Segons, 12.5, "Amb formatge i bacon"),
        Product("p4", "Entrecot a la Brasa", ProductCategory.Segons, 18.0, "Amb guarnició"),
        Product("p5", "Crema Catalana", ProductCategory.Postres, 5.5, "Cremada al moment"),
        Product("p6", "Coca-Cola", ProductCategory.Begudes, 2.5, "Refresc de 33cl")
    )
}

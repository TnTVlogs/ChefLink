package me.sergidalmau.cheflink.domain.repository
 
import kotlinx.coroutines.flow.Flow

import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.models.UserRole

interface OrderRepository {
    suspend fun createOrder(order: Order)
    suspend fun getPendingOrders(): List<Order>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus)
    suspend fun deleteOrder(orderId: String)
    suspend fun checkHealth(): Boolean
    fun observeUpdates(): Flow<Unit>
    fun getUpdateStatus(): Flow<Boolean>
    suspend fun clearCache()
}

interface TableRepository {
    suspend fun getTables(): List<Table>
    suspend fun createTable(number: Int, capacity: Int): Table
    suspend fun updateTable(number: Int, capacity: Int)
    suspend fun deleteTable(number: Int)
}

interface UserRepository {
    suspend fun login(username: String, password: String): User?
    suspend fun register(username: String, password: String, firstName: String, lastName: String, email: String, role: UserRole): User
    suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean
}

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun createProduct(name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean = true): Product
    suspend fun updateProduct(id: String, name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean)
    suspend fun deleteProduct(id: String)
}

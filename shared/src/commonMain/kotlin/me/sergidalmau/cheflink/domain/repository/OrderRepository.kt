package me.sergidalmau.cheflink.domain.repository
 
import kotlinx.coroutines.flow.Flow

import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus


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


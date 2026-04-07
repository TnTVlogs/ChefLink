package me.sergidalmau.cheflink.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import me.sergidalmau.cheflink.data.local.DatabaseFactory
import me.sergidalmau.cheflink.data.local.OrderItemsTable
import me.sergidalmau.cheflink.data.local.OrdersTable
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderItem
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import me.sergidalmau.cheflink.domain.repository.OrderRepository

class OrderRepository : OrderRepository {

    override suspend fun createOrder(order: Order) = DatabaseFactory.dbQuery {
        val exists = OrdersTable.selectAll().where { OrdersTable.id eq order.id }.any()

        if (exists) {
            OrdersTable.update({ OrdersTable.id eq order.id }) {
                it[tableNumber] = order.tableNumber
                it[waiterName] = order.waiterName
                it[status] = order.status.name
                it[timestamp] = order.timestamp
                it[notes] = order.notes
            }
            OrderItemsTable.deleteWhere { OrderItemsTable.orderId eq order.id }
        } else {
            OrdersTable.insert {
                it[id] = order.id
                it[tableNumber] = order.tableNumber
                it[waiterName] = order.waiterName
                it[status] = order.status.name
                it[timestamp] = order.timestamp
                it[notes] = order.notes
            }
        }

        OrderItemsTable.batchInsert(order.items) { item ->
            this[OrderItemsTable.orderId] = order.id
            this[OrderItemsTable.productId] = item.product.id.substring(1).toIntOrNull() ?: 0
            this[OrderItemsTable.productName] = item.product.name
            this[OrderItemsTable.quantity] = item.quantity
            this[OrderItemsTable.priceAtTime] = item.priceAtTime
            this[OrderItemsTable.notes] = item.notes
        }
        Unit
    }

    override suspend fun getPendingOrders(): List<Order> = DatabaseFactory.dbQuery {
        OrdersTable.selectAll()
            .map { row ->
                val orderId = row[OrdersTable.id]
                val items = OrderItemsTable.selectAll()
                    .where { OrderItemsTable.orderId eq orderId }
                    .map { itemRow ->
                        OrderItem(
                            product = Product(
                                id = "p${itemRow[OrderItemsTable.productId]}",
                                name = itemRow[OrderItemsTable.productName],
                                category = ProductCategory.Primers, 
                                price = itemRow[OrderItemsTable.priceAtTime]
                            ),
                            quantity = itemRow[OrderItemsTable.quantity],
                            priceAtTime = itemRow[OrderItemsTable.priceAtTime],
                            notes = itemRow[OrderItemsTable.notes]
                        )
                    }

                Order(
                    id = orderId,
                    tableNumber = row[OrdersTable.tableNumber],
                    waiterName = row[OrdersTable.waiterName],
                    items = items,
                    status = OrderStatus.valueOf(row[OrdersTable.status]),
                    timestamp = row[OrdersTable.timestamp],
                    notes = row[OrdersTable.notes]
                )
            }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) = DatabaseFactory.dbQuery {
        OrdersTable.update({ OrdersTable.id eq orderId }) {
            it[status] = newStatus.name
        }
        Unit
    }

    override suspend fun deleteOrder(orderId: String) = DatabaseFactory.dbQuery {
        OrderItemsTable.deleteWhere { OrderItemsTable.orderId eq orderId }
        OrdersTable.deleteWhere { OrdersTable.id eq orderId }
        Unit
    }

    override suspend fun checkHealth(): Boolean = true
    override fun observeUpdates(): Flow<Unit> = emptyFlow()
    override fun getUpdateStatus(): Flow<Boolean> = flowOf(true)
    override suspend fun clearCache() {}
}

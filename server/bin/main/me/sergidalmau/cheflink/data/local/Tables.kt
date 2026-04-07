package me.sergidalmau.cheflink.data.local

import org.jetbrains.exposed.v1.core.Table

object OrdersTable : Table("orders") {
    val id = varchar("id", 50)
    val tableNumber = integer("table_number")
    val waiterName = varchar("waiter_name", 100)
    val status = varchar("status", 20)
    val timestamp = long("timestamp")
    val notes = text("notes").nullable()

    override val primaryKey = PrimaryKey(id)
}

object OrderItemsTable : Table("order_items") {
    val id = integer("id").autoIncrement()
    val orderId = varchar("order_id", 50) references OrdersTable.id
    val productId = integer("product_id")
    val productName = varchar("name", 100)
    val quantity = integer("quantity")
    val priceAtTime = double("price_at_time")
    val notes = text("notes").nullable()

    override val primaryKey = PrimaryKey(id)
}

object UsersTable : Table("users") {
    val id = varchar("id", 50)
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 100)
    val firstName = varchar("first_name", 100).default("")
    val lastName = varchar("last_name", 100).default("")
    val email = varchar("email", 100).default("")
    val role = varchar("role", 20)
    override val primaryKey = PrimaryKey(id)
}

object TablesTable : Table("tables_persistence") {
    val number = integer("number")
    val capacity = integer("capacity")
    val status = varchar("status", 20)
    override val primaryKey = PrimaryKey(number)
}

object ProductsTable : Table("products") {
    val id = varchar("id", 50)
    val name = varchar("name", 100)
    val category = varchar("category", 50)
    val price = double("price")
    val description = text("description").nullable()
    val isAvailable = bool("is_available").default(true)
    override val primaryKey = PrimaryKey(id)
}

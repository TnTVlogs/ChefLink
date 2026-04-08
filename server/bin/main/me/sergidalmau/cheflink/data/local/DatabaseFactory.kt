package me.sergidalmau.cheflink.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.sergidalmau.cheflink.domain.models.MockData
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.domain.models.Table as MyTable
import me.sergidalmau.cheflink.domain.models.TableStatus
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.mindrot.jbcrypt.BCrypt

object DatabaseFactory {
    private lateinit var database: Database

    fun init() {
        val dbUrl = System.getenv("DB_URL")
        val dbUser = System.getenv("DB_USER") ?: ""
        val dbPassword = System.getenv("DB_PASSWORD") ?: ""

        if (dbUrl != null) {
            println("Server: Using MariaDB at $dbUrl")
            val driverClassName = "org.mariadb.jdbc.Driver"
            database = Database.connect(dbUrl, driverClassName, user = dbUser, password = dbPassword)
        } else {
            println("Server: Using local SQLite database")
            val driverClassName = "org.sqlite.JDBC"
            val jdbcURL = "jdbc:sqlite:./cheflink.db"
            database = Database.connect(jdbcURL, driverClassName)
        }

        transaction(database) {
            SchemaUtils.create(OrdersTable, OrderItemsTable, UsersTable, TablesTable, ProductsTable)

            MigrationUtils.statementsRequiredForDatabaseMigration(
                OrdersTable,
                OrderItemsTable,
                UsersTable,
                TablesTable,
                ProductsTable
            ).forEach { statement ->
                exec(statement)
            }

            val adminExists = UsersTable.selectAll().where { UsersTable.username eq "admin" }.any()
            if (!adminExists) {
                UsersTable.insert {
                    it[id] = "u-admin"
                    it[username] = "admin"
                    it[passwordHash] = BCrypt.hashpw("admin", BCrypt.gensalt())
                    it[role] = UserRole.Admin.name
                }
            }

            if (ProductsTable.selectAll().count() == 0L) {
                println("Server: Seeding products from MockData")
                MockData.products.forEach { p ->
                    ProductsTable.insert {
                        it[id] = p.id
                        it[name] = p.name
                        it[category] = p.category.name
                        it[price] = p.price
                        it[description] = p.description
                    }
                }
            }

            if (TablesTable.selectAll().count() == 0L) {
                listOf(
                    MyTable(1, 4, TableStatus.Lliure),
                    MyTable(2, 2, TableStatus.Ocupada),
                    MyTable(3, 6, TableStatus.Lliure),
                    MyTable(4, 4, TableStatus.Lliure),
                    MyTable(5, 2, TableStatus.Ocupada),
                    MyTable(6, 4, TableStatus.Lliure)
                ).forEach { table ->
                    TablesTable.insert {
                        it[TablesTable.number] = table.number
                        it[TablesTable.capacity] = table.capacity
                        it[TablesTable.status] = table.status.name
                    }
                }
            }
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            suspendTransaction(database) {
                block()
            }
        }
}

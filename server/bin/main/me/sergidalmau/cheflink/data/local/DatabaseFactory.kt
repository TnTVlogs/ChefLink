package me.sergidalmau.cheflink.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.cdimascio.dotenv.Dotenv
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
import java.security.SecureRandom
import me.sergidalmau.cheflink.domain.util.HashUtils
import kotlinx.coroutines.runBlocking

object DatabaseFactory {
    private lateinit var database: Database
    private const val PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#%?"

    private fun generatePassword(length: Int = 20): String {
        val random = SecureRandom()
        return buildString(length) {
            repeat(length) {
                append(PASSWORD_ALPHABET[random.nextInt(PASSWORD_ALPHABET.length)])
            }
        }
    }

    fun init(env: Dotenv? = null) {
        val dbUrl = env?.get("DB_URL") ?: System.getenv("DB_URL")
        val dbUser = env?.get("DB_USER") ?: System.getenv("DB_USER") ?: ""
        val dbPassword = env?.get("DB_PASSWORD") ?: System.getenv("DB_PASSWORD") ?: ""

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
            SchemaUtils.create(OrdersTable, OrderItemsTable, UsersTable, TablesTable, ProductsTable, RefreshTokensTable)

            MigrationUtils.statementsRequiredForDatabaseMigration(
                OrdersTable,
                OrderItemsTable,
                UsersTable,
                TablesTable,
                ProductsTable,
                RefreshTokensTable
            ).forEach { statement ->
                exec(statement)
            }

            val adminExists = UsersTable.selectAll().where { UsersTable.username eq "admin" }.any()
            if (!adminExists) {
                val configuredAdminPassword = env?.get("ADMIN_PASSWORD") ?: System.getenv("ADMIN_PASSWORD")
                val adminPassword = configuredAdminPassword?.takeIf { it.isNotBlank() } ?: generatePassword()
                val adminPasswordHash = BCrypt.hashpw(runBlocking { HashUtils.sha256(adminPassword) }, BCrypt.gensalt())
                UsersTable.insert {
                    it[id] = "u-admin"
                    it[username] = "admin"
                    it[passwordHash] = adminPasswordHash
                    it[role] = UserRole.Admin.name
                }
                if (configuredAdminPassword.isNullOrBlank()) {
                    println("Server: Generated initial admin password for user 'admin': $adminPassword")
                    println("Server: Set ADMIN_PASSWORD in the environment to control the bootstrap password.")
                } else {
                    println("Server: Created bootstrap admin user 'admin' from ADMIN_PASSWORD.")
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

package me.sergidalmau.cheflink.data.repository

import me.sergidalmau.cheflink.data.local.DatabaseFactory
import me.sergidalmau.cheflink.data.local.TablesTable
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.domain.models.TableStatus
import me.sergidalmau.cheflink.domain.repository.TableRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class TableRepositoryImpl : TableRepository {
    override suspend fun getTables(): List<Table> = DatabaseFactory.dbQuery {
        TablesTable.selectAll().map {
            Table(
                number = it[TablesTable.number],
                capacity = it[TablesTable.capacity],
                status = TableStatus.Lliure 
            )
        }
    }

    override suspend fun createTable(number: Int, capacity: Int): Table = DatabaseFactory.dbQuery {
        TablesTable.insert {
            it[TablesTable.number] = number
            it[TablesTable.capacity] = capacity
            it[TablesTable.status] = TableStatus.Lliure.name
        }
        Table(number, capacity, TableStatus.Lliure)
    }

    override suspend fun updateTable(number: Int, capacity: Int) = DatabaseFactory.dbQuery {
        TablesTable.update({ TablesTable.number eq number }) {
            it[TablesTable.capacity] = capacity
        }
        Unit
    }

    override suspend fun deleteTable(number: Int) = DatabaseFactory.dbQuery {
        TablesTable.deleteWhere { TablesTable.number eq number }
        Unit
    }
}

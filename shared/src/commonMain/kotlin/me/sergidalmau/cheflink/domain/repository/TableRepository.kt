package me.sergidalmau.cheflink.domain.repository

import me.sergidalmau.cheflink.domain.models.Table

interface TableRepository {
    suspend fun getTables(): List<Table>
    suspend fun createTable(number: Int, capacity: Int): Table
    suspend fun updateTable(number: Int, capacity: Int)
    suspend fun deleteTable(number: Int)
}

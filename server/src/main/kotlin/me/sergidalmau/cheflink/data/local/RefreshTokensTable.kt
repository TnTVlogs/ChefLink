package me.sergidalmau.cheflink.data.local

import org.jetbrains.exposed.v1.core.Table

object RefreshTokensTable : Table("refresh_tokens") {
    val id = integer("id").autoIncrement()
    val token = varchar("token", 255).uniqueIndex()
    val userId = varchar("user_id", 50) references UsersTable.id
    val expiresAt = long("expires_at")
    
    override val primaryKey = PrimaryKey(id)
}

package me.sergidalmau.cheflink.domain.util

import io.ktor.util.*

object HashUtils {
    suspend fun sha256(text: String): String {
        val digest = Digest("SHA-256")
        digest += text.encodeToByteArray()
        return hex(digest.build())
    }
}

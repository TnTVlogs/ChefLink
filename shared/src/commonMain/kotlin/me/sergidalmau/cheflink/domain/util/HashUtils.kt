package me.sergidalmau.cheflink.domain.util

import io.ktor.util.*

object HashUtils {
    /**
     * Hashes a string using SHA-256 and returns the hex representation.
     */
    suspend fun sha256(text: String): String {
        // Use Ktor's Digest which is consistent across platforms
        val digest = Digest("SHA-256")
        digest += text.encodeToByteArray()
        return hex(digest.build())
    }
}

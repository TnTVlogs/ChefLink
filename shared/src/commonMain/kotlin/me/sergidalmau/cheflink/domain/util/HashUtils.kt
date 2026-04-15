package me.sergidalmau.cheflink.domain.util

import io.ktor.util.sha256
import io.ktor.util.hex

object HashUtils {
    /**
     * Hashes a string using SHA-256 and returns the hex representation.
     */
    fun sha256(text: String): String {
        return hex(sha256(text.encodeToByteArray()))
    }
}

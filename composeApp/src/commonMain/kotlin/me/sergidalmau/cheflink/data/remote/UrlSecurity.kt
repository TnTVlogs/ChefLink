package me.sergidalmau.cheflink.data.remote

import io.ktor.http.Url

private fun isLocalHost(host: String): Boolean {
    val normalized = host.lowercase()
    if (normalized == "localhost" || normalized == "::1") return true
    val parts = normalized.split(".").mapNotNull { it.toIntOrNull() }
    if (parts.size != 4) return false
    return when {
        parts[0] == 127 -> true                                          // 127.0.0.0/8
        parts[0] == 10 -> true                                           // 10.0.0.0/8
        parts[0] == 172 && parts[1] in 16..31 -> true                   // 172.16.0.0/12
        parts[0] == 192 && parts[1] == 168 -> true                      // 192.168.0.0/16
        else -> false
    }
}

internal fun isLocalUrl(url: String): Boolean {
    if (url.isBlank()) return true
    return try {
        isLocalHost(Url(url).host)
    } catch (_: Exception) {
        false
    }
}

fun requireSecureRemoteBaseUrl(baseUrl: String, purpose: String) {
    val url = Url(baseUrl)
    val isHttp = url.protocol.name.equals("http", ignoreCase = true)
    if (isHttp && !isLocalHost(url.host)) {
        error("$purpose requires HTTPS for non-local servers: $baseUrl")
    }
}

fun toWebSocketUrl(baseUrl: String, path: String): String {
    val url = Url(baseUrl)
    val protocol = when (url.protocol.name.lowercase()) {
        "https" -> "wss"
        "http" -> "ws"
        else -> error("Unsupported base URL protocol: ${url.protocol.name}")
    }
    val normalizedPath = if (path.startsWith("/")) path else "/$path"
    val portPart = when {
        url.port == url.protocol.defaultPort -> ""
        else -> ":${url.port}"
    }
    return "$protocol://${url.host}$portPart$normalizedPath"
}

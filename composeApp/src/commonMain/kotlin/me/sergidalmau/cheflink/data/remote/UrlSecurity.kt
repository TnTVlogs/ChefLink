package me.sergidalmau.cheflink.data.remote

import io.ktor.http.Url

private fun isLoopbackHost(host: String): Boolean {
    val normalized = host.lowercase()
    return normalized == "localhost" || normalized == "127.0.0.1" || normalized == "::1"
}

fun requireSecureRemoteBaseUrl(baseUrl: String, purpose: String) {
    val url = Url(baseUrl)
    val isHttp = url.protocol.name.equals("http", ignoreCase = true)
    if (isHttp && !isLoopbackHost(url.host)) {
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

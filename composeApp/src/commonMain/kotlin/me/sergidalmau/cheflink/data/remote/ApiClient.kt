package me.sergidalmau.cheflink.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.sergidalmau.cheflink.ui.util.AppSession
import me.sergidalmau.cheflink.domain.models.RefreshRequest
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import me.sergidalmau.cheflink.data.repository.SettingsRepository

object ApiClient {
    val client = HttpClient(CIO) {
        engine {
            maxConnectionsCount = 50
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val access = AppSession.accessToken.value
                    val refresh = AppSession.refreshToken.value
                    if (access != null && refresh != null) {
                        BearerTokens(access, refresh)
                    } else null
                }

                refreshTokens {
                    val refresh = AppSession.refreshToken.value ?: return@refreshTokens null

                    try {
                        // Use a separate client or a basic request to avoid infinite recursion
                        // Use the BASE_URL since it's available in this package
                        val response = client.post("$BASE_URL/refresh") {
                            markAsRefreshTokenRequest()
                            setBody(RefreshRequest(refresh))
                            contentType(ContentType.Application.Json)
                        }.body<Map<String, String>>()

                        val newAccess = response["accessToken"] ?: return@refreshTokens null
                        val newRefresh = response["refreshToken"]
                            ?: refresh // Fallback to old if not rotated, but server now rotates

                        // Persist rotated tokens
                        AppSession.updateAccessToken(newAccess)
                        AppSession.updateRefreshToken(newRefresh)

                        val settings = SettingsRepository()
                        settings.accessToken = newAccess
                        settings.refreshToken = newRefresh

                        BearerTokens(newAccess, newRefresh)
                    } catch (_: Exception) {
                        // Critical: if refresh fails, session is dead. Clean up memory and disk.
                        AppSession.logout()
                        val settings = SettingsRepository()
                        settings.accessToken = null
                        settings.refreshToken = null
                        settings.persistedUser = null
                        null
                    }
                }

                sendWithoutRequest { request ->
                    val path = request.url.encodedPath
                    !path.contains("login") && !path.contains("register") && !path.contains("refresh") && !path.contains(
                        "health"
                    )
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        install(WebSockets) {
            pingIntervalMillis = 20_000 // 20 seconds
        }
    }
}
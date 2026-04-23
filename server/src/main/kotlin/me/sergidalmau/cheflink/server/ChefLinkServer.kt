package me.sergidalmau.cheflink.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import me.sergidalmau.cheflink.data.local.DatabaseFactory
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.http.*
import io.ktor.server.response.*
import kotlin.time.Duration.Companion.seconds

object ChefLinkServer {
    private var engine: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    fun start(): Boolean {
        if (engine != null) return true

        return try {
            val (env, envSource) = EnvLoader.load()
            if (envSource.startsWith("(missing")) {
                println("WARNING: .env not found. $envSource")
            } else {
                println("Server: Loaded .env from $envSource")
            }

            DatabaseFactory.init(env)
            DiscoveryService.start()
            
            if (env["JWT_SECRET"] == null) {
                println("WARNING: JWT_SECRET is missing. Using defaults.")
            }
            
            val tokenManager = TokenManager(env)
            
            val host = env["SERVER_HOST"] ?: "0.0.0.0"
            val port = env["SERVER_PORT"]?.toIntOrNull() ?: 8080
            
            engine = embeddedServer(Netty, port = port, host = host) {
                install(Authentication) {
                    jwt("auth-jwt") {
                        verifier(tokenManager.getVerifier())
                        validate { credential ->
                            val userId = credential.payload.getClaim("userId").asString()
                            if (!userId.isNullOrEmpty()) {
                                JWTPrincipal(credential.payload)
                            } else {
                                null
                            }
                        }
                        challenge { _, _ ->
                            call.respond(HttpStatusCode.Unauthorized, "Token is invalid or expired")
                        }
                    }
                }
                
                install(ContentNegotiation) {
                    json()
                }
                install(WebSockets) {
                    pingPeriod = 15.seconds
                    timeout = 15.seconds
                    maxFrameSize = Long.MAX_VALUE
                    masking = false
                }
                routing {
                    ordersRoutes(tokenManager)
                }
            }.start(wait = false)
            
            println("Servidor ChefLink integrat i corrent!")
            true
        } catch (e: Exception) {
            println("Error en iniciar el servidor: ${e.message}")
            engine = null
            false
        }
    }

    fun stop() {
        engine?.stop(1000, 5000)
        engine = null
    }

    fun isRunning(): Boolean = engine != null
}

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
import kotlin.time.Duration.Companion.seconds

object ChefLinkServer {
    private var engine: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    fun start(): Boolean {
        if (engine != null) return true

        return try {
            DatabaseFactory.init()
            DiscoveryService.start()
            
            engine = embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
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
                    ordersRoutes()
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

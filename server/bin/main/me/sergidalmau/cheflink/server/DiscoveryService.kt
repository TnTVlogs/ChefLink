package me.sergidalmau.cheflink.server

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.NetworkInterface
import kotlin.concurrent.thread

object DiscoveryService {
    private var socket: DatagramSocket? = null
    private var running = false
    private const val PORT = 8888
    private const val DISCOVERY_REQUEST = "DISCOVER_CHEFLINK_REQUEST"
    private const val DISCOVERY_RESPONSE_PREFIX = "CHEFLINK_SERVER_RESPONSE|"

    fun start() {
        if (running) return
        running = true

        thread(isDaemon = true, name = "ChefLink-Discovery") {
            try {
                socket = DatagramSocket(PORT).apply {
                    broadcast = true
                }
                val buffer = ByteArray(1024)

                println("DiscoveryService: Escoltant peticions UDP al port $PORT")

                while (running) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)

                    val message = String(packet.data, 0, packet.length)
                    if (message == DISCOVERY_REQUEST) {
                        val localIp = getLocalIp() ?: "localhost"
                        val response = "$DISCOVERY_RESPONSE_PREFIX http://$localIp:8080"
                        val responseData = response.toByteArray()
                        val responsePacket = DatagramPacket(
                            responseData,
                            responseData.size,
                            packet.address,
                            packet.port
                        )
                        socket?.send(responsePacket)
                        println("DiscoveryService: Resposta enviada a ${packet.address}:${packet.port} -> $response")
                    }
                }
            } catch (e: Exception) {
                if (running) {
                    println("DiscoveryService Error: ${e.message}")
                }
            }
        }
    }

    private fun getLocalIp(): String? {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces().asSequence()
                .filter { ni ->
                    !ni.isLoopback && ni.isUp &&
                            !ni.displayName.contains("VirtualBox", ignoreCase = true) &&
                            !ni.displayName.contains("VMware", ignoreCase = true) &&
                            !ni.displayName.contains("Pseudo", ignoreCase = true) &&
                            !ni.displayName.contains("Teredo", ignoreCase = true) &&
                            !ni.displayName.contains("vEthernet", ignoreCase = true)
                }
                .flatMap { it.inetAddresses.asSequence() }
                .filter { !it.isLoopbackAddress && it is Inet4Address }
                .map { it.hostAddress }
                .toList()

            println("DiscoveryService: Interfícies detectades: $interfaces")

            interfaces.find { it.startsWith("192.168.") } ?: interfaces.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }
}

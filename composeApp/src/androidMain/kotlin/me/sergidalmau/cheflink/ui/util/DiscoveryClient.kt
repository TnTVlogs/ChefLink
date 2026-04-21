package me.sergidalmau.cheflink.ui.util

import android.content.Context
import android.net.wifi.WifiManager
import me.sergidalmau.cheflink.data.util.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException
import kotlin.time.Duration.Companion.milliseconds

actual class DiscoveryClient actual constructor() {
    private val PORT = 8888
    private val TIMEOUT = 2000
    private val DISCOVERY_REQUEST = "DISCOVER_CHEFLINK_REQUEST"
    private val DISCOVERY_RESPONSE_PREFIX = "CHEFLINK_SERVER_RESPONSE|"

    actual suspend fun discover(): String? = withContext(Dispatchers.IO) {
        val wifiManager = appContext?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val lock = wifiManager?.createMulticastLock("ChefLinkDiscoveryLock")
        
        var socket: DatagramSocket? = null
        var attempts = 0
        val maxAttempts = 3
        
        try {
            lock?.acquire()
            while (attempts < maxAttempts) {
            attempts++
            println("DiscoveryClient: Intent de descobriment $attempts/$maxAttempts...")
            try {
                if (socket == null) {
                    socket = DatagramSocket().apply {
                        broadcast = true
                        soTimeout = TIMEOUT
                    }
                }

                val broadcastAddress = getBroadcastAddress() ?: InetAddress.getByName("255.255.255.255")
                println("DiscoveryClient: Usant adreça de broadcast $broadcastAddress")

                val requestData = DISCOVERY_REQUEST.toByteArray()
                val requestPacket = DatagramPacket(
                    requestData,
                    requestData.size,
                    broadcastAddress,
                    PORT
                )

                socket.send(requestPacket)

                val buffer = ByteArray(1024)
                val responsePacket = DatagramPacket(buffer, buffer.size)

                socket.receive(responsePacket)
                val response = String(responsePacket.data, 0, responsePacket.length)
                
                if (response.startsWith(DISCOVERY_RESPONSE_PREFIX)) {
                    val url = response.substringAfter(DISCOVERY_RESPONSE_PREFIX).trim()
                    println("DiscoveryClient: Servidor trobat a $url")
                    return@withContext url
                }
            } catch (_: SocketTimeoutException) {
                println("DiscoveryClient: Timeout a l'intent $attempts")
            } catch (e: Exception) {
                println("DiscoveryClient Error: ${e.message}")
            }
            
            if (attempts < maxAttempts) {
                kotlinx.coroutines.delay(500.milliseconds)
            }
        }
        } finally {
            lock?.release()
            socket?.close()
        }
        
        println("DiscoveryClient: No s'ha trobat cap servidor després de $maxAttempts intents.")
        null
    }

    private fun getBroadcastAddress(): InetAddress? {
        return try {
            NetworkInterface.getNetworkInterfaces().asSequence()
                .flatMap { it.interfaceAddresses.asSequence() }
                .filter { it.broadcast != null }
                .map { it.broadcast }
                .firstOrNull()
        } catch (_: Exception) {
            null
        }
    }
}

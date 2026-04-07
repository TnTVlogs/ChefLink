package me.sergidalmau.cheflink.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import me.sergidalmau.cheflink.data.util.getLocalData
import me.sergidalmau.cheflink.data.util.saveLocalData
import me.sergidalmau.cheflink.data.remote.ApiClient
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.domain.repository.OrderRepository
import java.util.Collections
import kotlin.time.Duration.Companion.milliseconds

@Serializable
sealed class PendingAction {
    @Serializable
    data class Create(val order: Order) : PendingAction()
    @Serializable
    data class UpdateStatus(val id: String, val status: OrderStatus) : PendingAction()
    @Serializable
    data class Delete(val id: String) : PendingAction()
}

class RemoteOrderRepository(private val baseUrl: String) : OrderRepository {
    private val client = ApiClient.client
    private val _connectionState = MutableStateFlow(true)
    private val pendingActions = Collections.synchronizedList(mutableListOf<PendingAction>())
    private var cachedOrders = mutableListOf<Order>()

    init {
        loadOfflineData()
    }

    private fun loadOfflineData() {
        try {
            getLocalData("pending_actions")?.let { json ->
                val actions = Json.decodeFromString<List<PendingAction>>(json)
                pendingActions.addAll(actions)
            }
            getLocalData("cached_orders")?.let { json ->
                val orders = Json.decodeFromString<List<Order>>(json)
                cachedOrders = orders.toMutableList()
            }
        } catch (e: Exception) {
            println("Error loading offline data: ${e.message}")
        }
    }

    private fun saveOfflineData() {
        try {
            val actionsJson = Json.encodeToString(pendingActions.toList())
            saveLocalData("pending_actions", actionsJson)
            
            val ordersJson = Json.encodeToString(cachedOrders)
            saveLocalData("cached_orders", ordersJson)
        } catch (e: Exception) {
            println("Error saving offline data: ${e.message}")
        }
    }

    override suspend fun clearCache() {
        cachedOrders.clear()
        pendingActions.clear()
        saveOfflineData()
    }

    override suspend fun createOrder(order: Order) {
        // Mark as un-synced initially
        val offlineOrder = order.copy(isSynced = false)
        
        try {
            client.post("$baseUrl/orders") {
                contentType(ContentType.Application.Json)
                setBody(order)
            }
            _connectionState.value = true
        } catch (e: Exception) {
            println("Queueing createOrder for ${order.id}: ${e.message}")
            _connectionState.value = false
            pendingActions.add(PendingAction.Create(offlineOrder))
            
            // Update cache immediately for UI feedback
            val index = cachedOrders.indexOfFirst { it.id == order.id }
            if (index != -1) {
                cachedOrders[index] = offlineOrder
            } else {
                cachedOrders.add(0, offlineOrder)
            }
            saveOfflineData()
        }
    }

    override suspend fun getPendingOrders(): List<Order> {
        return try {
            val orders: List<Order> = client.get("$baseUrl/orders").body()
            _connectionState.value = true
            cachedOrders = orders.toMutableList()
            saveOfflineData()
            orders
        } catch (e: Exception) {
            println("Error fetching orders, returning cache: ${e.message}")
            _connectionState.value = false
            
            // Merge cached orders with pending creations
            val pendingCreations = pendingActions.filterIsInstance<PendingAction.Create>().map { it.order }
            val merged = (cachedOrders + pendingCreations).distinctBy { it.id }
            merged
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        try {
            client.post("$baseUrl/orders/$orderId/status") {
                contentType(ContentType.Application.Json)
                setBody(newStatus)
            }
            _connectionState.value = true
        } catch (e: Exception) {
            println("Queueing updateStatus for $orderId: ${e.message}")
            _connectionState.value = false
            pendingActions.add(PendingAction.UpdateStatus(orderId, newStatus))
            
            // Update cache immediately for UI feedback
            val index = cachedOrders.indexOfFirst { it.id == orderId }
            if (index != -1) {
                cachedOrders[index] = cachedOrders[index].copy(status = newStatus, isSynced = false)
            }
            saveOfflineData()
        }
    }

    override suspend fun deleteOrder(orderId: String) {
        try {
            client.delete("$baseUrl/orders/$orderId")
            _connectionState.value = true
        } catch (e: Exception) {
            println("Queueing deleteOrder for $orderId: ${e.message}")
            _connectionState.value = false
            pendingActions.add(PendingAction.Delete(orderId))
            
            // Update cache immediately (hide it)
            cachedOrders.removeAll { it.id == orderId }
            saveOfflineData()
        }
    }

    private suspend fun processPendingActions() {
        if (pendingActions.isEmpty()) return
        println("Processing ${pendingActions.size} pending actions...")
        val iterator = pendingActions.iterator()
        while (iterator.hasNext()) {
            val action = iterator.next()
            try {
                when (action) {
                    is PendingAction.Create -> {
                        client.post("$baseUrl/orders") {
                            contentType(ContentType.Application.Json)
                            setBody(action.order.copy(isSynced = true))
                        }
                    }
                    is PendingAction.UpdateStatus -> {
                        client.post("$baseUrl/orders/${action.id}/status") {
                            contentType(ContentType.Application.Json)
                            setBody(action.status)
                        }
                    }
                    is PendingAction.Delete -> {
                        client.delete("$baseUrl/orders/${action.id}")
                    }
                }
                iterator.remove()
                saveOfflineData()
            } catch (e: Exception) {
                println("Failed to sync action, will retry later: ${e.message}")
                _connectionState.value = false
                break // Stop processing on first failure
            }
        }
    }

    override suspend fun checkHealth(): Boolean {
        return try {
            val response = client.get("$baseUrl/health")
            val healthy = response.status.value in 200..299
            if (healthy && !_connectionState.value) {
                println("Health: Connection restored!")
                processPendingActions()
            }
            _connectionState.value = healthy
            healthy
        } catch (e: Exception) {
            println("Health: Check failed ($baseUrl/health): ${e.message}")
            _connectionState.value = false
            false
        }
    }

    override fun observeUpdates(): Flow<Unit> = channelFlow {
        val wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://")
        while (isActive) {
            try {
                client.webSocket("$wsUrl/orders/updates") {
                    println("WebSocket: Connected to $wsUrl/orders/updates")
                    
                    // Force an immediate refresh upon connection/reconnection
                    trySend(Unit)
                    
                    _connectionState.value = true
                    processPendingActions()

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            if (text == "orders_updates" || text == "orders_updated") {
                                println("WebSocket: Received orders_updated")
                                trySend(Unit)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("WebSocket: Connection lost or failed ($e). Reconnecting in 3s...")
                _connectionState.value = false
            }
            if (isActive) delay(3000.milliseconds)
        }
    }

    override fun getUpdateStatus(): Flow<Boolean> = _connectionState.asStateFlow()
}

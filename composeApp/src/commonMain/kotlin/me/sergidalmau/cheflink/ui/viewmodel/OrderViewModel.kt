package me.sergidalmau.cheflink.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.sergidalmau.cheflink.data.repository.RemoteOrderRepository
import me.sergidalmau.cheflink.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.domain.repository.OrderRepository
import me.sergidalmau.cheflink.ui.util.AppSession

class OrderViewModel(
    settingsRepository: SettingsRepository = SettingsRepository()
) : ViewModel() {
    private var repository: OrderRepository = RemoteOrderRepository(settingsRepository.serverUrl)
    private var updateObservationJob: Job? = null
    private var statusObservationJob: Job? = null
    private var sessionObservationJob: Job? = null
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _isConnected = MutableStateFlow(true)
    val isConnected = _isConnected.asStateFlow()

    private val _filterStatuses = MutableStateFlow<Set<OrderStatus>>(emptySet())
    val filterStatuses = _filterStatuses.asStateFlow()

    private val _filterTable = MutableStateFlow<Int?>(null)
    val filterTable = _filterTable.asStateFlow()

    private val statusPriority = mapOf(
        OrderStatus.PENDING to 0,
        OrderStatus.PREPARING to 1,
        OrderStatus.READY to 2,
        OrderStatus.ENVIADA to 3,
        OrderStatus.SERVIDA to 4,
        OrderStatus.CANCELLED to 5
    )

    val filteredOrders = combine(_orders, _filterStatuses, _filterTable) { orders, statuses, table ->
        orders.filter { order ->
            (statuses.isEmpty() || statuses.contains(order.status)) &&
                    (table == null || order.tableNumber == table)
        }.sortedWith(
            compareBy<Order> { statusPriority[it.status] ?: 99 }
                .thenByDescending { it.timestamp }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        sessionObservationJob = viewModelScope.launch {
            var lastToken: String? = null
            AppSession.accessToken.collect { token ->
                if (token == lastToken) return@collect
                lastToken = token

                if (token.isNullOrBlank()) {
                    stopObservations()
                    _orders.value = emptyList()
                    _isConnected.value = false
                } else {
                    startObservations()
                    refreshOrders()
                }
            }
        }
    }

    private fun stopObservations() {
        updateObservationJob?.cancel()
        statusObservationJob?.cancel()
        updateObservationJob = null
        statusObservationJob = null
    }

    private fun startObservations() {
        if (updateObservationJob != null || statusObservationJob != null) return
        updateObservationJob?.cancel()
        statusObservationJob?.cancel()

        updateObservationJob = viewModelScope.launch {
            repository.observeUpdates().collect {
                refreshOrders()
            }
        }

        statusObservationJob = viewModelScope.launch {
            repository.getUpdateStatus().collect {
                _isConnected.value = it
            }
        }
    }

    private var currentRepoUrl: String? = null

    fun setServerUrl(url: String) {
        if (url == currentRepoUrl) return
        currentRepoUrl = url
        repository = RemoteOrderRepository(url)
    }

    fun refreshOrders() {
        viewModelScope.launch {
            try {
                _orders.value = repository.getPendingOrders()
            } catch (e: Exception) {
                println("Error fetching orders: ${e.message}")
            }
        }
    }

    fun sendOrder(order: Order) {
        viewModelScope.launch {
            try {
                repository.createOrder(order)
                refreshOrders()
            } catch (e: Exception) {
                println("Error sending order: ${e.message}")
            }
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            try {
                repository.deleteOrder(orderId)
                refreshOrders()
            } catch (e: Exception) {
                println("Error deleting order: ${e.message}")
            }
        }
    }

    fun updateStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, newStatus)
                refreshOrders()
            } catch (e: Exception) {
                println("Error updating status: ${e.message}")
            }
        }
    }

    fun toggleStatusFilter(status: OrderStatus) {
        val current = _filterStatuses.value.toMutableSet()
        if (current.contains(status)) current.remove(status)
        else current.add(status)
        _filterStatuses.value = current
    }

    fun setTableFilter(tableNumber: Int?) {
        _filterTable.value = tableNumber
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                repository.clearCache()
                refreshOrders()
            } catch (e: Exception) {
                println("Error clearing cache: ${e.message}")
            }
        }
    }
}

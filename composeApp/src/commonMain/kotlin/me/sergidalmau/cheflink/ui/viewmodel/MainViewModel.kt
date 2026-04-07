package me.sergidalmau.cheflink.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.repository.TableRepository
import me.sergidalmau.cheflink.domain.repository.UserRepository
import me.sergidalmau.cheflink.domain.repository.OrderRepository
import me.sergidalmau.cheflink.data.repository.RemoteTableRepository
import me.sergidalmau.cheflink.data.repository.RemoteUserRepository
import me.sergidalmau.cheflink.data.repository.RemoteOrderRepository
import me.sergidalmau.cheflink.data.repository.RemoteProductRepository
import me.sergidalmau.cheflink.data.repository.SettingsRepository
import me.sergidalmau.cheflink.ui.util.DiscoveryClient
import kotlinx.coroutines.Job
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.domain.repository.ProductRepository
import me.sergidalmau.cheflink.ui.util.ComponentSize
import me.sergidalmau.cheflink.ui.util.Language
import kotlin.time.Duration.Companion.milliseconds

class MainViewModel(
    private val settingsRepository: SettingsRepository = SettingsRepository()
) : ViewModel() {
    private var tableRepository: TableRepository = RemoteTableRepository(settingsRepository.serverUrl)
    private var userRepository: UserRepository = RemoteUserRepository(settingsRepository.serverUrl)
    private var orderRepository: OrderRepository = RemoteOrderRepository(settingsRepository.serverUrl)
    private var productRepository: ProductRepository = RemoteProductRepository(settingsRepository.serverUrl)

    private val _serverUrl = MutableStateFlow(settingsRepository.serverUrl)
    val serverUrl = _serverUrl.asStateFlow()

    private val _isDarkMode = MutableStateFlow(settingsRepository.isDarkMode)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow(settingsRepository.language)
    val language = _language.asStateFlow()

    private val _componentSize = MutableStateFlow(settingsRepository.componentSize)
    val componentSize = _componentSize.asStateFlow()

    private val _isServerEnabled = MutableStateFlow(settingsRepository.isServerEnabled)
    val isServerEnabled = _isServerEnabled.asStateFlow()

    private var updateObservationJob: Job? = null
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables = _tables.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _isApiHealthy = MutableStateFlow<Boolean?>(null) // null = initial/checking, true/false = result
    val isApiHealthy = _isApiHealthy.asStateFlow()

    private val _healthErrorMessage = MutableStateFlow<String?>(null)
    val healthErrorMessage = _healthErrorMessage.asStateFlow()

    private val _isCheckingHealth = MutableStateFlow(false)
    val isCheckingHealth = _isCheckingHealth.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _registrationMessage = MutableStateFlow<String?>(null)
    val registrationMessage = _registrationMessage.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    // Mode Selection States (Desktop Only)
    private val _isModeSelected = MutableStateFlow(me.sergidalmau.cheflink.getPlatform().name.contains("Android"))
    val isModeSelected = _isModeSelected.asStateFlow()

    private val _isServerStarting = MutableStateFlow(false)
    val isServerStarting = _isServerStarting.asStateFlow()

    private val _serverStartError = MutableStateFlow<String?>(null)
    val serverStartError = _serverStartError.asStateFlow()

    private val serverManager = me.sergidalmau.cheflink.ui.util.getPlatformServerManager()

    init {
        if (_isModeSelected.value) {
            checkApiHealth()
            loadProducts()
            startObservingUpdates()
        }
    }

    private fun startObservingUpdates() {
        updateObservationJob?.cancel()
        updateObservationJob = viewModelScope.launch {
            orderRepository.observeUpdates().collect {
                loadTables()
                loadProducts()
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = productRepository.getProducts()
            } catch (_: Exception) {}
        }
    }

    fun createProduct(name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean = true) {
        viewModelScope.launch {
            try {
                productRepository.createProduct(name, category, price, description, isAvailable)
                loadProducts()
            } catch (_: Exception) {}
        }
    }

    fun updateProduct(id: String, name: String, category: ProductCategory, price: Double, description: String?, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                productRepository.updateProduct(id, name, category, price, description, isAvailable)
                loadProducts()
            } catch (_: Exception) {}
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(id)
                loadProducts()
            } catch (_: Exception) {}
        }
    }

    fun checkApiHealth(retryCount: Int = 3) {
        viewModelScope.launch {
            _isCheckingHealth.value = true
            _healthErrorMessage.value = null
            
            var healthy = false
            var lastError: String? = null
            
            for (i in 1..retryCount) {
                try {
                    healthy = orderRepository.checkHealth()
                    if (healthy) break
                } catch (e: Exception) {
                    lastError = e.message ?: "Error de connexió desconegut"
                }
                delay(1000.milliseconds)
            }
            
            _isApiHealthy.value = healthy
            _isCheckingHealth.value = false
            
            if (healthy) {
                _healthErrorMessage.value = null
                loadTables()
                loadProducts()
            } else {
                _healthErrorMessage.value = lastError ?: "No s'ha obtingut resposta del servidor"
            }
        }
    }

    fun loadTables() {
        viewModelScope.launch {
            _tables.value = tableRepository.getTables()
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginError.value = null
            val loggedInUser = userRepository.login(username, password)
            if (loggedInUser != null) {
                _user.value = loggedInUser
            } else {
                _loginError.value = "Credencials invàlides. Torna-ho a provar."
            }
        }
    }

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess = _registrationSuccess.asStateFlow()

    fun register(
        username: String, 
        password: String, 
        firstName: String, 
        lastName: String, 
        email: String, 
        role: UserRole
    ) {
        viewModelScope.launch {
            _registrationMessage.value = null
            _registrationSuccess.value = false
            try {
                userRepository.register(username, password, firstName, lastName, email, role)
                _registrationMessage.value = "Usuari $username registrat correctament."
                _registrationSuccess.value = true
            } catch (_: ResponseException) {
                // This covers 400, 404, 500 etc.
                _registrationMessage.value = "Error: El compte ja existeix o dades invàlides."
            } catch (e: Exception) {
                _registrationMessage.value = "Error en registrar l'usuari: ${e.message}"
            }
        }
    }

    fun resetRegistrationStatus() {
        _registrationSuccess.value = false
        _registrationMessage.value = null
    }

    fun clearRegistrationMessage() {
        _registrationMessage.value = null
    }

    fun logout() {
        _user.value = null
        _loginError.value = null
    }

    fun changePassword(oldPass: String, newPass: String) {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            try {
                val success = userRepository.changePassword(currentUser.id, oldPass, newPass)
                if (success) {
                    _registrationMessage.value = "Contrasenya canviada correctament."
                } else {
                    _registrationMessage.value = "Error: La contrasenya antiga no és correcta."
                }
            } catch (e: Exception) {
                _registrationMessage.value = "Error al canviar la contrasenya: ${e.message}"
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        settingsRepository.isDarkMode = enabled
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
        settingsRepository.language = lang
    }

    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
    }

    fun setComponentSize(size: ComponentSize) {
        _componentSize.value = size
        settingsRepository.componentSize = size
    }

    fun toggleServer(enabled: Boolean) {
        _isServerEnabled.value = enabled
        settingsRepository.isServerEnabled = enabled
        // Nota: A escriptori, això pot requerir reinici o una crida directa a ChefLinkServer
    }

    fun setServerUrl(url: String) {
        if (url == settingsRepository.serverUrl) return
        
        settingsRepository.serverUrl = url
        _serverUrl.value = url
        
        // Re-initialize repositories
        tableRepository = RemoteTableRepository(url)
        userRepository = RemoteUserRepository(url)
        orderRepository = RemoteOrderRepository(url)
        productRepository = RemoteProductRepository(url)
        
        // Restart everything
        checkApiHealth()
        startObservingUpdates()
    }

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering = _isDiscovering.asStateFlow()

    fun discoverServer() {
        viewModelScope.launch {
            _isDiscovering.value = true
            _registrationMessage.value = "Buscant servidor a la xarxa local..."
            val discoveredUrl = DiscoveryClient().discover()
            if (discoveredUrl != null) {
                setServerUrl(discoveredUrl)
                _isApiHealthy.value = true // Forçem a true per anar directe al login
                _healthErrorMessage.value = null
                _registrationMessage.value = "Servidor trobat i connectat!"
            } else {
                _registrationMessage.value = "No s'ha trobat cap servidor. Revisa que estiguis al mateix Wi-Fi."
            }
            _isDiscovering.value = false
        }
    }

    fun createTable(number: Int, capacity: Int) {
        viewModelScope.launch {
            try {
                tableRepository.createTable(number, capacity)
                loadTables()
            } catch (e: Exception) {
                println("Error creating table: ${e.message}")
            }
        }
    }

    fun updateTable(number: Int, capacity: Int) {
        viewModelScope.launch {
            try {
                tableRepository.updateTable(number, capacity)
                loadTables()
            } catch (e: Exception) {
                println("Error updating table: ${e.message}")
            }
        }
    }

    fun deleteTable(number: Int) {
        viewModelScope.launch {
            try {
                tableRepository.deleteTable(number)
                loadTables()
            } catch (e: Exception) {
                println("Error deleting table: ${e.message}")
            }
        }
    }

    fun selectClientMode() {
        _isModeSelected.value = true
        checkApiHealth()
        loadProducts()
        startObservingUpdates()
    }

    fun selectHostMode() {
        viewModelScope.launch {
            _isServerStarting.value = true
            _serverStartError.value = null
            
            val result = serverManager?.startServer() ?: Result.failure(Exception("Server not supported on this platform"))
            
            if (result.isSuccess) {
                setServerUrl("http://localhost:8080")
                _isModeSelected.value = true
            } else {
                _serverStartError.value = result.exceptionOrNull()?.message ?: "Unknown error"
            }
            _isServerStarting.value = false
        }
    }

    fun retryHostMode() {
        selectHostMode()
    }
}

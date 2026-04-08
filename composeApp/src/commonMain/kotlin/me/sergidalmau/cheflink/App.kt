package me.sergidalmau.cheflink

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.ui.screens.login.LoginScreen
import me.sergidalmau.cheflink.ui.screens.order.OrderScreen
import me.sergidalmau.cheflink.ui.screens.orderslist.OrdersListScreen
import me.sergidalmau.cheflink.ui.screens.modeselection.ModeSelectionScreen
import me.sergidalmau.cheflink.ui.screens.settings.SettingsScreen
import me.sergidalmau.cheflink.ui.screens.tables.TablesScreen
import me.sergidalmau.cheflink.ui.theme.AppTheme
import me.sergidalmau.cheflink.ui.viewmodel.OrderViewModel
import me.sergidalmau.cheflink.ui.viewmodel.MainViewModel
import me.sergidalmau.cheflink.ui.util.Language
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.CatalanStrings
import me.sergidalmau.cheflink.ui.util.SpanishStrings
import me.sergidalmau.cheflink.ui.util.EnglishStrings
import me.sergidalmau.cheflink.ui.util.FrenchStrings
import androidx.compose.runtime.CompositionLocalProvider
import kotlin.time.Clock

enum class Screen {
    Tables, Order, OrdersList, Settings
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val orderViewModel: OrderViewModel = viewModel { OrderViewModel() }
    val mainViewModel: MainViewModel = viewModel { MainViewModel() }
    val isDarkMode by mainViewModel.isDarkMode.collectAsState()
    val language by mainViewModel.language.collectAsState()
    val serverUrl by mainViewModel.serverUrl.collectAsState()

    androidx.compose.runtime.LaunchedEffect(serverUrl) {
        orderViewModel.setServerUrl(serverUrl)
    }

    val strings = when (language) {
        Language.CA -> CatalanStrings
        Language.ES -> SpanishStrings
        Language.EN -> EnglishStrings
        Language.FR -> FrenchStrings
    }

    CompositionLocalProvider(LocalChefLinkStrings provides strings) {
        AppTheme(darkTheme = isDarkMode) {
            val user by mainViewModel.user.collectAsState()
            val tables by mainViewModel.tables.collectAsState()
            var currentScreen by remember { mutableStateOf(Screen.Tables) }
            var selectedTable by remember { mutableStateOf<Int?>(null) }
            var editingOrderId by remember { mutableStateOf<String?>(null) }

            val orders by orderViewModel.orders.collectAsState()
            val isApiHealthy by mainViewModel.isApiHealthy.collectAsState()
            val isCheckingHealth by mainViewModel.isCheckingHealth.collectAsState()
            val loginError by mainViewModel.loginError.collectAsState()
            val healthErrorMessage by mainViewModel.healthErrorMessage.collectAsState()
            val isEditMode by mainViewModel.isEditMode.collectAsState()
            val componentSize by mainViewModel.componentSize.collectAsState()

            val isModeSelected by mainViewModel.isModeSelected.collectAsState()
            val isServerStarting by mainViewModel.isServerStarting.collectAsState()
            val serverStartError by mainViewModel.serverStartError.collectAsState()

            if (!isModeSelected) {
                ModeSelectionScreen(
                    isStarting = isServerStarting,
                    error = serverStartError,
                    onHostSelected = { mainViewModel.selectHostMode() },
                    onClientSelected = { mainViewModel.selectClientMode() },
                    onRetry = { mainViewModel.retryHostMode() }
                )
            } else if (currentScreen == Screen.Settings) {
                // Es renderitzarà dins del Scaffold a sota
            } else if (isApiHealthy != true) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        if (isCheckingHealth || isApiHealthy == null) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isApiHealthy == null) strings.initializing else strings.connecting,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            // isApiHealthy is false
                            Text(
                                strings.serverNotFound,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            healthErrorMessage?.let {
                                Text(
                                    text = strings.connectionDetails(it),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Text(
                                strings.checkServerStatus,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            val isDiscovering by mainViewModel.isDiscovering.collectAsState()

                            if (isDiscovering) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(strings.discovering)
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { mainViewModel.checkApiHealth() },
                                        enabled = !isCheckingHealth,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(strings.retry)
                                    }

                                    Button(
                                        onClick = { mainViewModel.discoverServer() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        Text(strings.autoDiscover)
                                    }

                                    OutlinedButton(
                                        onClick = { currentScreen = Screen.Settings },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Settings, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(strings.settings)
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (user == null) {
                val registrationMessage by mainViewModel.registrationMessage.collectAsState()
                val registrationSuccess by mainViewModel.registrationSuccess.collectAsState()
                LoginScreen(
                    onLogin = { username, password -> mainViewModel.login(username, password) },
                    onRegister = { uname, pass, fname, lname, email, role ->
                        mainViewModel.register(uname, pass, fname, lname, email, role)
                    },
                    errorMessage = loginError,
                    registrationMessage = registrationMessage,
                    registrationSuccess = registrationSuccess,
                    onResetRegistrationStatus = { mainViewModel.resetRegistrationStatus() }
                )
            }

            // Si no estem en un dels estats anteriors (bloquejants), mostrem l'app normal amb Scaffold
            if (currentScreen == Screen.Settings || (isApiHealthy == true && user != null)) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        if (currentScreen != Screen.Order) {
                            val screenTitle = when (currentScreen) {
                                Screen.Tables -> strings.tables
                                Screen.OrdersList -> strings.orders
                                Screen.Settings -> strings.settings
                                else -> strings.appTitle
                            }

                            TopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            text = "ChefLink",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = screenTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                actions = {
                                    if (isEditMode) {
                                        TextButton(
                                            onClick = { mainViewModel.setEditMode(false) },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Text(strings.exitEditMode, style = MaterialTheme.typography.labelMedium)
                                        }
                                    } else {
                                        Text(
                                            user?.firstName ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (currentScreen != Screen.Order) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                tonalElevation = 0.dp
                            ) {
                                val items = listOf(
                                    Screen.Tables to Icons.Default.TableBar,
                                    Screen.OrdersList to Icons.AutoMirrored.Filled.ListAlt,
                                    Screen.Settings to Icons.Default.Settings
                                )

                                items.forEach { (screen, icon) ->
                                    val labelStr = when (screen) {
                                        Screen.Tables -> strings.tables
                                        Screen.OrdersList -> strings.orders
                                        Screen.Settings -> strings.settings
                                        else -> screen.name
                                    }
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                icon,
                                                contentDescription = labelStr,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        label = { Text(labelStr, style = MaterialTheme.typography.labelSmall) },
                                        selected = currentScreen == screen,
                                        onClick = {
                                            if (!isEditMode) {
                                                currentScreen = screen
                                                selectedTable = null
                                                editingOrderId = null
                                            }
                                        },
                                        enabled = !isEditMode
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues)) {
                        val isConnected by orderViewModel.isConnected.collectAsState()

                        Column {
                            if (!isConnected) {
                                Modifier.background(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                                ).let { bgModifier ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(bgModifier)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.CloudOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Sense connexió en temps real. Reconnectant...",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                when (currentScreen) {
                                    Screen.Tables -> {
                                        TablesScreen(
                                            tables = tables,
                                            orders = orders,
                                            isEditMode = isEditMode,
                                            componentSize = componentSize,
                                            onSelectTable = { tableNumber ->
                                                if (!isEditMode) {
                                                    selectedTable = tableNumber
                                                    currentScreen = Screen.Order
                                                }
                                            },
                                            onCreateTable = { number, capacity ->
                                                mainViewModel.createTable(
                                                    number,
                                                    capacity
                                                )
                                            },
                                            onUpdateTable = { number, capacity ->
                                                mainViewModel.updateTable(
                                                    number,
                                                    capacity
                                                )
                                            },
                                            onDeleteTable = { number -> mainViewModel.deleteTable(number) }
                                        )
                                    }

                                    Screen.Order -> {
                                        val orderToEdit = orders.find { it.id == editingOrderId }

                                        val products by mainViewModel.products.collectAsState()
                                        OrderScreen(
                                            tableNumber = selectedTable ?: orderToEdit?.tableNumber ?: 0,
                                            products = products,
                                            onSendOrder = { newItems, notes ->
                                                val newOrder = Order(
                                                    id = editingOrderId ?: "o${
                                                        Clock.System.now().toEpochMilliseconds()
                                                    }",
                                                    tableNumber = selectedTable ?: orderToEdit?.tableNumber ?: 0,
                                                    waiterName = if (!user?.firstName.isNullOrEmpty() || !user?.lastName.isNullOrEmpty()) {
                                                        "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim()
                                                    } else {
                                                        user?.username ?: "Unknown"
                                                    },
                                                    items = newItems,
                                                    status = orderToEdit?.status ?: OrderStatus.PENDING,
                                                    timestamp = Clock.System.now().toEpochMilliseconds(),
                                                    notes = notes.ifEmpty { null }
                                                )
                                                orderViewModel.sendOrder(newOrder)
                                                currentScreen = Screen.OrdersList
                                                selectedTable = null
                                                editingOrderId = null
                                            },
                                            onBack = {
                                                currentScreen =
                                                    if (editingOrderId != null) Screen.OrdersList else Screen.Tables
                                                selectedTable = null
                                                editingOrderId = null
                                            },
                                            initialItems = orderToEdit?.items ?: emptyList(),
                                            initialNotes = orderToEdit?.notes ?: ""
                                        )
                                    }

                                    Screen.OrdersList -> {
                                        OrdersListScreen(
                                            orderViewModel = orderViewModel,
                                            mainViewModel = mainViewModel,
                                            onUpdateStatus = { id, newStatus ->
                                                orderViewModel.updateStatus(id, newStatus)
                                            },
                                            onDeleteOrder = { id ->
                                                orderViewModel.deleteOrder(id)
                                            },
                                            onEditOrder = { order ->
                                                editingOrderId = order.id
                                                currentScreen = Screen.Order
                                            }
                                        )
                                    }

                                    Screen.Settings -> {
                                        val registrationMessage by mainViewModel.registrationMessage.collectAsState()
                                        SettingsScreen(
                                            user = user,
                                            isDarkMode = isDarkMode,
                                            onDarkModeChange = { mainViewModel.toggleDarkMode(it) },
                                            language = language,
                                            onLanguageChange = { mainViewModel.setLanguage(it) },
                                            componentSize = componentSize,
                                            onComponentSizeChange = { mainViewModel.setComponentSize(it) },
                                            onLogout = {
                                                mainViewModel.logout()
                                                currentScreen = Screen.Tables
                                            },
                                            onRegister = { uname, pass, fname, lname, email, role ->
                                                mainViewModel.register(uname, pass, fname, lname, email, role)
                                            },
                                            registrationMessage = registrationMessage,
                                            onClearRegistrationMessage = { mainViewModel.clearRegistrationMessage() },
                                            viewModel = mainViewModel,
                                            onChangePassword = { old, new ->
                                                mainViewModel.changePassword(old, new)
                                            },
                                            onClearCache = {
                                                orderViewModel.clearCache()
                                            },
                                            onEnterEditMode = {
                                                mainViewModel.setEditMode(true)
                                                currentScreen = Screen.Tables
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package me.sergidalmau.cheflink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.ui.util.dragScroll
import me.sergidalmau.cheflink.ui.util.Language
import me.sergidalmau.cheflink.ui.util.ComponentSize
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.automirrored.filled.List
import androidx.lifecycle.viewmodel.compose.viewModel
import me.sergidalmau.cheflink.ui.viewmodel.MainViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun SettingsScreen(
    user: User?,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    componentSize: ComponentSize,
    onComponentSizeChange: (ComponentSize) -> Unit,
    onLogout: () -> Unit,
    onRegister: (String, String, String, String, String, UserRole) -> Unit = { _, _, _, _, _, _ -> },
    registrationMessage: String? = null,
    onClearRegistrationMessage: () -> Unit = {},
    onChangePassword: (String, String) -> Unit = { _, _ -> },
    onClearCache: () -> Unit = {},
    onEnterEditMode: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val strings = LocalChefLinkStrings.current
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showProductManagement by remember { mutableStateOf(false) }

    val isDiscovering by viewModel.isDiscovering.collectAsState()

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .dragScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.settings,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )

            // Secció d'Usuari
            if (user != null) {
                Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            val fullName = if (user.firstName.isNotEmpty() || user.lastName.isNotEmpty()) {
                                "${user.firstName} ${user.lastName}".trim()
                            } else {
                                user.username
                            }

                            Text(
                                text = fullName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "@${user.username}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AssistChip(
                        onClick = {},
                        label = { Text(if (user.role == UserRole.Admin) strings.roleAdmin else strings.roleWaiter) },
                        shape = MaterialTheme.shapes.small,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showPasswordDialog = true },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                        ) {
                            Text(strings.changePassword, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge)
                        }
                        if (user.role == UserRole.Admin) {
                            Button(
                                onClick = { showRegisterDialog = true },
                                modifier = Modifier.weight(1f).height(56.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                            ) {
                                Text(strings.newUser, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }

            // Secció de Gestió (Admin)
            if (user?.role == UserRole.Admin) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = strings.editMode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onEnterEditMode,
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.editMode)
                            }
                            Button(
                                onClick = { showProductManagement = true },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.articles)
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column {
                    SettingItem(
                        icon = Icons.Default.Language,
                        title = strings.language,
                        subtitle = language.displayName,
                        control = {
                            var showLanguageDialog by remember { mutableStateOf(false) }
                            TextButton(onClick = { showLanguageDialog = true }) {
                                Text(language.displayName)
                            }
                            if (showLanguageDialog) {
                                AlertDialog(
                                    onDismissRequest = { showLanguageDialog = false },
                                    title = { Text(strings.language) },
                                    text = {
                                        Column {
                                            Language.entries.forEach { lang ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().clickable {
                                                        onLanguageChange(lang)
                                                        showLanguageDialog = false
                                                    }.padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    RadioButton(selected = language == lang, onClick = null)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(lang.displayName)
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showLanguageDialog = false }) { Text(strings.close) }
                                    }
                                )
                            }
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.componentSize,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = when (componentSize) {
                                    ComponentSize.SMALL -> strings.sizeSmall
                                    ComponentSize.MEDIUM -> strings.sizeMedium
                                    ComponentSize.LARGE -> strings.sizeLarge
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = componentSize.value,
                            onValueChange = { onComponentSizeChange(ComponentSize.fromFloat(it)) },
                            valueRange = 0f..2f,
                            steps = 1,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    SettingItem(
                        icon = Icons.Default.DarkMode,
                        title = strings.darkMode,
                        subtitle = strings.darkModeDesc,
                        control = {
                            Switch(checked = isDarkMode, onCheckedChange = onDarkModeChange)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secció de Xarxa
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = strings.networkConfig,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val platformName = remember { me.sergidalmau.cheflink.getPlatform().name }
                    if (platformName.contains("Desktop", ignoreCase = true)) {
                        val isServerEnabled by viewModel.isServerEnabled.collectAsState()
                        SettingItem(
                            icon = Icons.Default.Wifi,
                            title = strings.internalServer,
                            subtitle = strings.internalServerDesc,
                            control = {
                                Switch(
                                    checked = isServerEnabled,
                                    onCheckedChange = { viewModel.toggleServer(it) }
                                )
                            }
                        )
                        Text(
                            text = strings.restartRequired,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    
                    Button(
                        onClick = { viewModel.discoverServer() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isDiscovering,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        if (isDiscovering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(strings.discovering)
                        } else {
                            Icon(Icons.Default.Wifi, null)
                            Spacer(Modifier.width(8.dp))
                            Text(strings.autoDiscover)
                        }
                    }
                    
                    if (registrationMessage != null && !showRegisterDialog) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = registrationMessage,
                            color = if (registrationMessage.contains("Error") || registrationMessage.contains("No s'ha trobat")) 
                                MaterialTheme.colorScheme.error 
                            else 
                                MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Registration Dialog
            if (showRegisterDialog) {
                var newUsername by remember { mutableStateOf("") }
                var newPassword by remember { mutableStateOf("") }
                var newFirstName by remember { mutableStateOf("") }
                var newLastName by remember { mutableStateOf("") }
                var newEmail by remember { mutableStateOf("") }
                var selectedRole by remember { mutableStateOf(UserRole.Cambrer) }

                AlertDialog(
                    onDismissRequest = {
                        showRegisterDialog = false
                        onClearRegistrationMessage()
                    },
                    title = { Text("Registrar Nou Usuari") },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                            if (registrationMessage != null) {
                                Text(
                                    text = registrationMessage,
                                    color = if (registrationMessage.contains("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            OutlinedTextField(
                                value = newUsername,
                                onValueChange = { newUsername = it },
                                label = { Text(strings.username) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text(strings.password) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newFirstName,
                                onValueChange = { newFirstName = it },
                                label = { Text(strings.firstName) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newLastName,
                                onValueChange = { newLastName = it },
                                label = { Text(strings.lastName) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newEmail,
                                onValueChange = { newEmail = it },
                                label = { Text(strings.email) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("${strings.userRole}:", style = MaterialTheme.typography.titleSmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedRole == UserRole.Cambrer,
                                    onClick = { selectedRole = UserRole.Cambrer }
                                )
                                Text(strings.roleWaiter, modifier = Modifier.padding(start = 8.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                RadioButton(
                                    selected = selectedRole == UserRole.Admin,
                                    onClick = { selectedRole = UserRole.Admin }
                                )
                                Text(strings.roleAdmin, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newUsername.isNotBlank() && newPassword.isNotBlank()) {
                                    onRegister(
                                        newUsername,
                                        newPassword,
                                        newFirstName,
                                        newLastName,
                                        newEmail,
                                        selectedRole
                                    )
                                }
                            }
                        ) { Text(strings.register) }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showRegisterDialog = false
                            onClearRegistrationMessage()
                        }) { Text(strings.close) }
                    }
                )
            }

            // Change Password Dialog (Simplified)
            if (showPasswordDialog) {
                var oldPass by remember { mutableStateOf("") }
                var newPass by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { showPasswordDialog = false },
                    title = { Text(strings.changePassword) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = oldPass,
                                onValueChange = { oldPass = it },
                                label = { Text(strings.password) })
                            OutlinedTextField(
                                value = newPass,
                                onValueChange = { newPass = it },
                                label = { Text(strings.password) })
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            onChangePassword(oldPass, newPass)
                            showPasswordDialog = false
                        }) { Text(strings.changePassword) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPasswordDialog = false }) { Text(strings.cancel) }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.appInfo,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoStats(strings.version, "1.0.0")
                    InfoStats(strings.lastUpdate, "24 de febrer de 2026")
                    InfoStats(strings.developedBy, "Sergi Dalmau")
                }
            }

            if (showProductManagement) {
                ProductManagementDialog(
                    products = viewModel.products.collectAsState().value,
                    onClose = { showProductManagement = false },
                    onCreate = { n, c, p, d, a -> viewModel.createProduct(n, c, p, d, a) },
                    onUpdate = { id, n, c, p, d, a -> viewModel.updateProduct(id, n, c, p, d, a) },
                    onDelete = { id -> viewModel.deleteProduct(id) }
                )
            }

            if (user != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(strings.logout)
                    }
                    Button(
                        onClick = onClearCache,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(strings.clearCache)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClearCache,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.clearCache)
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        control()
    }
}

@Composable
fun InfoStats(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp), // Added small horizontal padding
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProductManagementDialog(
    products: List<Product>,
    onClose: () -> Unit,
    onCreate: (String, ProductCategory, Double, String?, Boolean) -> Unit,
    onUpdate: (String, String, ProductCategory, Double, String?, Boolean) -> Unit,
    onDelete: (String) -> Unit
) {
    val strings = LocalChefLinkStrings.current
    var showEditDialog by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.category.name.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(strings.articles)
                IconButton(onClick = { showEditDialog = Product("", "", ProductCategory.Primers, 0.0) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
        text = {
            Column(modifier = Modifier.height(400.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    placeholder = { Text(strings.filterByTable) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredProducts.size) { index ->
                        val product = filteredProducts[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showEditDialog = product }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = when(product.category) {
                                        ProductCategory.Primers -> strings.categoryPrimers
                                        ProductCategory.Segons -> strings.categorySegons
                                        ProductCategory.Postres -> strings.categoryPostres
                                        ProductCategory.Begudes -> strings.categoryBegudes
                                        ProductCategory.Menus -> strings.categoryMenus
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (product.isAvailable) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                )
                                if (!product.isAvailable) {
                                    Text(
                                        text = strings.outOfStock,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            Text(
                                "${product.price}€",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { onDelete(product.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        if (index < filteredProducts.size - 1) HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) { Text(strings.close) }
        }
    )

    if (showEditDialog != null) {
        val editingProduct = showEditDialog!!
        ArticleEditDialog(
            product = if (editingProduct.id.isEmpty()) null else editingProduct,
            onClose = { showEditDialog = null },
            onSave = { n, c, p, d, a ->
                if (editingProduct.id.isEmpty()) {
                    onCreate(n, c, p, d, a)
                } else {
                    onUpdate(editingProduct.id, n, c, p, d, a)
                }
                showEditDialog = null
            }
        )
    }
}

@Composable
fun ArticleEditDialog(
    product: Product?,
    onClose: () -> Unit,
    onSave: (String, ProductCategory, Double, String?, Boolean) -> Unit
) {
    val strings = LocalChefLinkStrings.current
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: ProductCategory.Primers) }
    var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "0.0") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var isAvailable by remember { mutableStateOf(product?.isAvailable ?: true) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(if (product == null) strings.addArticle else strings.editArticle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.articleName) },
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = when(category) {
                            ProductCategory.Primers -> strings.categoryPrimers
                            ProductCategory.Segons -> strings.categorySegons
                            ProductCategory.Postres -> strings.categoryPostres
                            ProductCategory.Begudes -> strings.categoryBegudes
                            ProductCategory.Menus -> strings.categoryMenus
                        },
                        onValueChange = {},
                        label = { Text(strings.articleCategory) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { categoryMenuExpanded = true }) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false }
                    ) {
                        ProductCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(when(cat) {
                                        ProductCategory.Primers -> strings.categoryPrimers
                                        ProductCategory.Segons -> strings.categorySegons
                                        ProductCategory.Postres -> strings.categoryPostres
                                        ProductCategory.Begudes -> strings.categoryBegudes
                                        ProductCategory.Menus -> strings.categoryMenus
                                    })
                                },
                                onClick = {
                                    category = cat
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text(strings.articlePrice) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(strings.articleDescription) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.articleAvailable)
                    Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(name, category, priceStr.toDoubleOrNull() ?: 0.0, description.ifEmpty { null }, isAvailable)
            }) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text(strings.cancel) }
        }
    )
}

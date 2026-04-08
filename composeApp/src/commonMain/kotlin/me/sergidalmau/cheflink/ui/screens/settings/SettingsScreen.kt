package me.sergidalmau.cheflink.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.ui.screens.settings.components.AdminManagementCard
import me.sergidalmau.cheflink.ui.screens.settings.components.AppInfoCard
import me.sergidalmau.cheflink.ui.screens.settings.components.AppearanceCard
import me.sergidalmau.cheflink.ui.screens.settings.components.ChangePasswordDialog
import me.sergidalmau.cheflink.ui.screens.settings.components.NetworkCard
import me.sergidalmau.cheflink.ui.screens.settings.components.ProductManagementDialog
import me.sergidalmau.cheflink.ui.screens.settings.components.RegisterUserDialog
import me.sergidalmau.cheflink.ui.screens.settings.components.UserProfileCard
import me.sergidalmau.cheflink.ui.util.ComponentSize
import me.sergidalmau.cheflink.ui.util.Language
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.dragScroll
import me.sergidalmau.cheflink.ui.viewmodel.MainViewModel

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
    val isDiscovering by viewModel.isDiscovering.collectAsState()
    val scrollState = rememberScrollState()

    var showRegisterDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showProductManagement by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).dragScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                strings.settings,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )

            if (user != null) {
                UserProfileCard(
                    user = user,
                    onChangePassword = { showPasswordDialog = true },
                    onRegisterUser = { showRegisterDialog = true }
                )
            }

            if (user?.role == UserRole.Admin) {
                AdminManagementCard(
                    onEnterEditMode = onEnterEditMode,
                    onShowProductManagement = { showProductManagement = true }
                )
            }

            AppearanceCard(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                language = language,
                onLanguageChange = onLanguageChange,
                componentSize = componentSize,
                onComponentSizeChange = onComponentSizeChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            NetworkCard(
                viewModel = viewModel,
                isDiscovering = isDiscovering,
                registrationMessage = registrationMessage,
                showRegisterDialog = showRegisterDialog
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppInfoCard()

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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showRegisterDialog) {
        RegisterUserDialog(
            registrationMessage = registrationMessage,
            onRegister = { u, p, f, l, e, r -> onRegister(u, p, f, l, e, r) },
            onDismiss = { showRegisterDialog = false; onClearRegistrationMessage() }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onConfirm = onChangePassword,
            onDismiss = { showPasswordDialog = false }
        )
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
}

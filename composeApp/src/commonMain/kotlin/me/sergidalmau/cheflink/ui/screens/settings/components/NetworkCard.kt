package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.getPlatform
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.viewmodel.MainViewModel

private const val DEFAULT_CLOUD_URL = "https://cheflink.sergidalmau.dev"

@Composable
fun NetworkCard(
    viewModel: MainViewModel,
    isDiscovering: Boolean,
    registrationMessage: String?,
    showRegisterDialog: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val strings = LocalChefLinkStrings.current

    val currentServerUrl by viewModel.serverUrl.collectAsState()
    val pendingServerUrl by viewModel.pendingServerUrl.collectAsState()

    val platformName = remember { getPlatform().name }
    val isDesktop = !platformName.contains("Android", ignoreCase = true)
    val isServerEnabled by viewModel.isServerEnabled.collectAsState()
    val isRunningAsHost by viewModel.isRunningAsHost.collectAsState()
    val isHostMode = isDesktop && (isRunningAsHost || isServerEnabled)

    var cloudUrl by remember { mutableStateOf(DEFAULT_CLOUD_URL) }
    var manualUrl by remember(currentServerUrl) { mutableStateOf(currentServerUrl) }
    var showManualConfirmDialog by remember { mutableStateOf(false) }

    // Dialog: local discovery trobat URL diferent
    if (pendingServerUrl != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelServerSwitch() },
            title = { Text(strings.serverFound) },
            text = {
                Column {
                    Text(strings.serverFoundAt)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        pendingServerUrl!!,
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    if (currentServerUrl.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${strings.currentConnection} $currentServerUrl",
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(strings.confirmServerChange)
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmServerSwitch() }) { Text(strings.change) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelServerSwitch() }) { Text(strings.cancel) }
            }
        )
    }

    // Dialog: canvi manual URL (mode host)
    if (showManualConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showManualConfirmDialog = false },
            title = { Text(strings.serverChange) },
            text = { Text("${strings.confirmConnectTo}\n$manualUrl") },
            confirmButton = {
                Button(onClick = {
                    showManualConfirmDialog = false
                    viewModel.setServerUrl(manualUrl)
                }) { Text(strings.connect) }
            },
            dismissButton = {
                TextButton(onClick = { showManualConfirmDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                strings.networkConfig,
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isDesktop) {
                SettingItem(
                    icon = Icons.Default.Wifi,
                    title = strings.internalServer,
                    subtitle = strings.internalServerDesc,
                    control = { Switch(checked = isServerEnabled, onCheckedChange = { viewModel.toggleServer(it) }) }
                )
                Text(
                    strings.restartRequired,
                    style = typography.labelSmall,
                    color = colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    thickness = 0.5.dp,
                    color = colorScheme.outlineVariant
                )
            }

            if (isHostMode) {
                // Mode host: camp URL manual + botó cerca (amb confirmació)
                Text(
                    strings.serverUrl,
                    style = typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = manualUrl,
                        onValueChange = { manualUrl = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("http://192.168.1.x:8080") },
                        textStyle = typography.bodySmall
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { showManualConfirmDialog = true },
                        enabled = manualUrl.isNotBlank() && manualUrl != currentServerUrl
                    ) { Text(strings.apply) }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.discoverServer() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !isDiscovering,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.secondaryContainer,
                        contentColor = colorScheme.onSecondaryContainer
                    )
                ) {
                    if (isDiscovering) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.discovering)
                    } else {
                        Icon(Icons.Default.Wifi, null)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.discoverAutomatic)
                    }
                }
            } else {
                // Bloc: Servidor local
                Text(
                    strings.lanNetwork,
                    style = typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { viewModel.discoverLocalServer() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !isDiscovering,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.secondaryContainer,
                        contentColor = colorScheme.onSecondaryContainer
                    )
                ) {
                    if (isDiscovering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = colorScheme.onSecondaryContainer,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(strings.discovering)
                    } else {
                        Icon(Icons.Default.Wifi, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(strings.discoverLocal)
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = colorScheme.outlineVariant)
                Spacer(Modifier.height(16.dp))

                // Bloc: Núvol
                Text(
                    strings.cloud,
                    style = typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = cloudUrl,
                    onValueChange = { cloudUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(strings.cloudServerUrl) },
                    textStyle = typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.connectToCloudUrl(cloudUrl) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !isDiscovering && cloudUrl.isNotBlank()
                ) {
                    if (isDiscovering) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(strings.connecting)
                    } else {
                        Icon(Icons.Default.Cloud, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(strings.connectToCloud)
                    }
                }
            }

            if (registrationMessage != null && !showRegisterDialog) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = registrationMessage,
                    color = if (registrationMessage.contains("Error") || registrationMessage.contains("No s'ha"))
                        colorScheme.error else colorScheme.primary,
                    style = typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

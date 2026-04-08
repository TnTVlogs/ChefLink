package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.getPlatform
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.viewmodel.MainViewModel

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

            val platformName = remember { getPlatform().name }
            if (platformName.contains("Desktop", ignoreCase = true)) {
                val isServerEnabled by viewModel.isServerEnabled.collectAsState()
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

            Button(
                onClick = { viewModel.discoverServer() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isDiscovering,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondaryContainer,
                    contentColor = colorScheme.onSecondaryContainer
                )
            ) {
                if (isDiscovering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colorScheme.onSecondaryContainer,
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
                        colorScheme.error else colorScheme.primary,
                    style = typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

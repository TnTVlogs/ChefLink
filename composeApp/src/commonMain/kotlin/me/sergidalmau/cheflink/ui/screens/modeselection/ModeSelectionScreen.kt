package me.sergidalmau.cheflink.ui.screens.modeselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.ui.screens.modeselection.components.ModeCard
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun ModeSelectionScreen(
    isStarting: Boolean,
    error: String?,
    onHostSelected: () -> Unit,
    onClientSelected: () -> Unit,
    onRetry: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isStarting) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(24.dp))
                Text(
                    text = strings.startingServer,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (error != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 400.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = strings.serverStartError,
                    style = typography.titleLarge,
                    color = colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = error,
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = onRetry) {
                    Text(strings.retry)
                }
                TextButton(
                    onClick = onClientSelected,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(strings.modeClient)
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ChefLink",
                    style = typography.displayMedium,
                    color = colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = strings.selectMode,
                    style = typography.titleLarge,
                    modifier = Modifier.padding(bottom = 48.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModeCard(
                        title = strings.modeHost,
                        description = strings.modeHostDesc,
                        icon = Icons.Default.Computer,
                        onClick = onHostSelected
                    )

                    Spacer(Modifier.width(32.dp))

                    ModeCard(
                        title = strings.modeClient,
                        description = strings.modeClientDesc,
                        icon = Icons.Default.Cloud,
                        onClick = onClientSelected
                    )
                }
            }
        }
    }
}

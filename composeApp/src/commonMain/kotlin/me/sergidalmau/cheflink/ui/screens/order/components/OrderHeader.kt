package me.sergidalmau.cheflink.ui.screens.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun OrderHeader(
    tableNumber: Int,
    totalItems: Int,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onQuickSend: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "${strings.orderTitle} - ${strings.table} $tableNumber",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                BadgedBox(badge = {
                    if (totalItems > 0) Badge { Text(totalItems.toString()) }
                }) {
                    IconButton(onClick = onOpenCart) {
                        Icon(Icons.Default.ShoppingCart, "Cart", modifier = Modifier.size(24.dp))
                    }
                }

                if (totalItems > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onQuickSend) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            "Quick Send",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

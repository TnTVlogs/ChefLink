package me.sergidalmau.cheflink.ui.screens.orderslist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.ui.screens.orderslist.translatedName
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun StatusFilterDialog(
    selectedStatuses: List<OrderStatus>,
    onStatusToggle: (OrderStatus) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ListAlt, null)
                Text(strings.filterByStatus)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OrderStatus.entries.forEach { status ->
                    val isSelected = selectedStatuses.contains(status)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusToggle(status) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        Text(
                            status.translatedName(strings),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close)
            }
        }
    )
}

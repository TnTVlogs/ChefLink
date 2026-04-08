package me.sergidalmau.cheflink.ui.screens.orderslist.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.ui.screens.orderslist.translatedName
import me.sergidalmau.cheflink.ui.theme.successColor
import me.sergidalmau.cheflink.ui.theme.warningColor
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.formatPrice
import kotlin.time.Instant

@Composable
fun OrderCard(
    order: Order,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    var showStatusDialog by remember { mutableStateOf(false) }
    val strings = LocalChefLinkStrings.current
    val success = successColor()
    val warning = warningColor()

    val statusColor = when (order.status) {
        OrderStatus.PENDING -> warning
        OrderStatus.ENVIADA -> colorScheme.primary
        OrderStatus.SERVIDA -> success
        else -> colorScheme.outline
    }
    val statusLabel = order.status.translatedName(strings)

    val total = remember(order.items) { order.items.sumOf { it.product.price * it.quantity } }
    val timeString = remember(order.timestamp) {
        val instant = Instant.fromEpochMilliseconds(order.timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!order.isSynced) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "No sincronitzat",
                            tint = colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AssistChip(
                        onClick = {},
                        label = { Text("${strings.table} ${order.tableNumber}") },
                        leadingIcon = {
                            Icon(Icons.Default.TableBar, null, Modifier.size(16.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = colorScheme.primaryContainer)
                    )

                    AssistChip(
                        onClick = { showStatusDialog = true },
                        label = { Text(statusLabel) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = statusColor.copy(alpha = 0.2f),
                            labelColor = statusColor
                        ),
                        border = null
                    )
                }

                if (showStatusDialog) {
                    AlertDialog(
                        onDismissRequest = { showStatusDialog = false },
                        title = { Text("Canviar Estat") },
                        text = {
                            Column {
                                OrderStatus.entries.forEach { status ->
                                    TextButton(
                                        onClick = {
                                            onUpdateStatus(status)
                                            showStatusDialog = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(status.name)
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = { showStatusDialog = false }) {
                                Text("Cancel·lar")
                            }
                        }
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edit", tint = colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Delete", tint = colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    null,
                    Modifier.size(16.dp),
                    tint = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timeString,
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${strings.waiter}: ${order.waiterName}",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "${item.quantity}x ${item.product.name}",
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${(item.product.price * item.quantity).formatPrice()}€",
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!item.notes.isNullOrEmpty()) {
                            Text(
                                text = item.notes!!,
                                style = typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (!order.notes.isNullOrEmpty()) {
                val warning = warningColor()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(warning.copy(alpha = 0.1f), shape = shapes.small)
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Note, null, Modifier.size(16.dp), tint = warning)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${strings.notes}: ${order.notes!!}", style = typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${strings.total}:", style = typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "${total.formatPrice()}€",
                    style = typography.titleLarge,
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

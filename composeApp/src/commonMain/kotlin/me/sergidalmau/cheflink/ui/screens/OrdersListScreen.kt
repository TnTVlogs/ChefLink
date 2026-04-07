package me.sergidalmau.cheflink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.ui.theme.successColor
import me.sergidalmau.cheflink.ui.theme.warningColor
import me.sergidalmau.cheflink.ui.util.dragScroll
import me.sergidalmau.cheflink.ui.util.formatPrice
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.ui.text.style.TextAlign
import me.sergidalmau.cheflink.ui.viewmodel.MainViewModel
import me.sergidalmau.cheflink.ui.viewmodel.OrderViewModel
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.Strings

fun OrderStatus.translatedName(strings: Strings): String = when (this) {
    OrderStatus.PENDING -> strings.statusPending
    OrderStatus.PREPARING -> strings.statusPreparing
    OrderStatus.READY -> strings.statusReady
    OrderStatus.ENVIADA -> strings.statusSent
    OrderStatus.SERVIDA -> strings.statusServed
    OrderStatus.CANCELLED -> strings.statusCancelled
}

@Composable
fun OrdersListScreen(
    orderViewModel: OrderViewModel,
    mainViewModel: MainViewModel,
    onUpdateStatus: (String, OrderStatus) -> Unit,
    onDeleteOrder: (String) -> Unit,
    onEditOrder: (Order) -> Unit
) {
    val selectedStatuses by orderViewModel.filterStatuses.collectAsState()
    val filterTable by orderViewModel.filterTable.collectAsState()
    val orders by orderViewModel.filteredOrders.collectAsState()
    val tables by mainViewModel.tables.collectAsState()
    var deleteConfirmOrder by remember { mutableStateOf<String?>(null) }
    var refreshing by remember { mutableStateOf(false) }
    val strings = LocalChefLinkStrings.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (refreshing) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        
        Spacer(modifier = Modifier.height(16.dp))

        // Row de Filtres
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var showTableFilterDialog by remember { mutableStateOf(false) }
            var showStatusFilterDialog by remember { mutableStateOf(false) }

            // Botó per filtrar per taula
            FilterChip(
                selected = filterTable != null,
                onClick = { showTableFilterDialog = true },
                label = { Text(if (filterTable == null) strings.tables else "${strings.table} $filterTable") },
                leadingIcon = { Icon(Icons.Default.TableBar, null, modifier = Modifier.size(18.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )

            // Botó per filtrar per estat
            FilterChip(
                selected = selectedStatuses.size < OrderStatus.entries.size,
                onClick = { showStatusFilterDialog = true },
                label = { 
                    val label = when {
                        selectedStatuses.isEmpty() -> strings.allStatuses
                        selectedStatuses.size == OrderStatus.entries.size -> strings.allStatuses
                        selectedStatuses.size == 1 -> selectedStatuses.firstOrNull()?.name ?: ""
                        else -> strings.numStatuses(selectedStatuses.size)
                    }
                    Text(label)
                },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ListAlt, null, modifier = Modifier.size(18.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            // Diàlegs
            if (showTableFilterDialog) {
                TableFilterDialog(
                    tables = tables,
                    selectedTable = filterTable,
                    onSelectTable = { 
                        orderViewModel.setTableFilter(it)
                        showTableFilterDialog = false
                    },
                    onDismiss = { showTableFilterDialog = false }
                )
            }

            if (showStatusFilterDialog) {
                StatusFilterDialog(
                    selectedStatuses = selectedStatuses.toList(),
                    onStatusToggle = { status ->
                        orderViewModel.toggleStatusFilter(status)
                    },
                    onDismiss = { showStatusFilterDialog = false }
                )
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(strings.noOrders, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            BoxWithConstraints {
                val isPC = maxWidth > 800.dp
                if (isPC) {
                    val gridState = rememberLazyGridState()
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 380.dp),
                        state = gridState,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.dragScroll(gridState)
                    ) {
                        items(orders, key = { it.id }) { order ->
                            OrderCard(
                                order = order,
                                onDelete = { deleteConfirmOrder = order.id },
                                onEdit = { onEditOrder(order) },
                                onUpdateStatus = { newStatus -> onUpdateStatus(order.id, newStatus) }
                            )
                        }
                    }
                } else {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.dragScroll(listState)
                    ) {
                        items(orders, key = { it.id }) { order ->
                            OrderCard(
                                order = order,
                                onDelete = { deleteConfirmOrder = order.id },
                                onEdit = { onEditOrder(order) },
                                onUpdateStatus = { newStatus -> onUpdateStatus(order.id, newStatus) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (deleteConfirmOrder != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmOrder = null },
            title = { Text(strings.deleteOrder) },
            text = { Text(strings.deleteOrderConfirm) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteOrder(deleteConfirmOrder!!)
                        deleteConfirmOrder = null
                    },
                    colors = textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.delete)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmOrder = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}


@Composable
fun OrderCard(
    order: Order,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    val strings = LocalChefLinkStrings.current
    val success = successColor()
    val warning = warningColor()

    val statusColor = when (order.status) {
        OrderStatus.PENDING -> warning
        OrderStatus.ENVIADA -> MaterialTheme.colorScheme.primary
        OrderStatus.SERVIDA -> success
        else -> MaterialTheme.colorScheme.outline
    }
    val statusLabel = order.status.translatedName(strings)

    val total = remember(order.items) { order.items.sumOf { it.product.price * it.quantity } }
    // Format timestamp
    val timeString = remember(order.timestamp) {
        val instant = Instant.fromEpochMilliseconds(order.timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AssistChip(
                        onClick = {},
                        label = { Text("${strings.table} ${order.tableNumber}") },
                        leadingIcon = {
                            Icon(Icons.Default.TableBar, null, Modifier.size(16.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
                        Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    null,
                    Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${strings.waiter}: ${order.waiterName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${(item.product.price * item.quantity).formatPrice()}€",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!item.notes.isNullOrEmpty()) {
                            Text(
                                text = item.notes!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        .background(warning.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small)
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Note, null, Modifier.size(16.dp), tint = warning)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${strings.notes}: ${order.notes!!}", style = MaterialTheme.typography.bodySmall)
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
                Text("${strings.total}:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "${total.formatPrice()}€",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TableFilterDialog(
    tables: List<Table>,
    selectedTable: Int?,
    onSelectTable: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.TableBar, null)
                Text(LocalChefLinkStrings.current.filterByTable)
            }
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                Text(
                    LocalChefLinkStrings.current.filterByTableDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 64.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        FilterChip(
                            selected = selectedTable == null,
                            onClick = { onSelectTable(null) },
                            label = { 
                                Text(
                                    LocalChefLinkStrings.current.allTables, 
                                    modifier = Modifier.fillMaxWidth(), 
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    softWrap = false
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    
                    items(tables.sortedBy { it.number }) { table ->
                        val isSelected = selectedTable == table.number
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectTable(table.number) },
                            label = { Text("${table.number}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tancar")
            }
        }
    )
}

@Composable
fun StatusFilterDialog(
    selectedStatuses: List<OrderStatus>,
    onStatusToggle: (OrderStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ListAlt, null)
                Text(LocalChefLinkStrings.current.filterByStatus)
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
                        androidx.compose.material3.Checkbox(
                            checked = isSelected,
                            onCheckedChange = null // Handled by row clickable
                        )
                        Text(status.translatedName(LocalChefLinkStrings.current), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalChefLinkStrings.current.close)
            }
        }
    )
}

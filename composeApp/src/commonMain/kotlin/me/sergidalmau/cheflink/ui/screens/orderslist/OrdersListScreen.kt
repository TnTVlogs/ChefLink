package me.sergidalmau.cheflink.ui.screens.orderslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.foundation.layout.BoxWithConstraints
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.ui.screens.orderslist.components.DeleteOrderDialog
import me.sergidalmau.cheflink.ui.screens.orderslist.components.OrderCard
import me.sergidalmau.cheflink.ui.screens.orderslist.components.OrdersFilterRow
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.Strings
import me.sergidalmau.cheflink.ui.util.dragScroll
import me.sergidalmau.cheflink.ui.viewmodel.MainViewModel
import me.sergidalmau.cheflink.ui.viewmodel.OrderViewModel

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
    val colorScheme = MaterialTheme.colorScheme
    val selectedStatuses by orderViewModel.filterStatuses.collectAsState()
    val filterTable by orderViewModel.filterTable.collectAsState()
    val orders by orderViewModel.filteredOrders.collectAsState()
    val tables by mainViewModel.tables.collectAsState()
    var deleteConfirmOrder by remember { mutableStateOf<String?>(null) }
    val strings = LocalChefLinkStrings.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        OrdersFilterRow(
            filterTable = filterTable,
            selectedStatuses = selectedStatuses.toList(),
            tables = tables,
            onTableFilterChange = { orderViewModel.setTableFilter(it) },
            onStatusToggle = { orderViewModel.toggleStatusFilter(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(strings.noOrders, color = colorScheme.onSurfaceVariant)
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
                                onUpdateStatus = { onUpdateStatus(order.id, it) })
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
                                onUpdateStatus = { onUpdateStatus(order.id, it) })
                        }
                    }
                }
            }
        }
    }

    if (deleteConfirmOrder != null) {
        DeleteOrderDialog(
            onConfirm = { onDeleteOrder(deleteConfirmOrder!!); deleteConfirmOrder = null },
            onDismiss = { deleteConfirmOrder = null }
        )
    }
}

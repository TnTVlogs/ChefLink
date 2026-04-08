package me.sergidalmau.cheflink.ui.screens.orderslist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.OrderStatus
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun OrdersFilterRow(
    filterTable: Int?,
    selectedStatuses: List<OrderStatus>,
    tables: List<Table>,
    onTableFilterChange: (Int?) -> Unit,
    onStatusToggle: (OrderStatus) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val strings = LocalChefLinkStrings.current
    var showTableFilterDialog by remember { mutableStateOf(false) }
    var showStatusFilterDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = filterTable != null,
            onClick = { showTableFilterDialog = true },
            label = { Text(if (filterTable == null) strings.tables else "${strings.table} $filterTable") },
            leadingIcon = { Icon(Icons.Default.TableBar, null, modifier = Modifier.size(18.dp)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = colorScheme.secondaryContainer,
                selectedLabelColor = colorScheme.onSecondaryContainer
            )
        )
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
                selectedContainerColor = colorScheme.primaryContainer,
                selectedLabelColor = colorScheme.onPrimaryContainer
            )
        )
    }

    if (showTableFilterDialog) {
        TableFilterDialog(
            tables = tables,
            selectedTable = filterTable,
            onSelectTable = { onTableFilterChange(it); showTableFilterDialog = false },
            onDismiss = { showTableFilterDialog = false }
        )
    }
    if (showStatusFilterDialog) {
        StatusFilterDialog(
            selectedStatuses = selectedStatuses,
            onStatusToggle = onStatusToggle,
            onDismiss = { showStatusFilterDialog = false }
        )
    }
}

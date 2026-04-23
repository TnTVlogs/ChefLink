package me.sergidalmau.cheflink.ui.screens.tables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.ui.screens.tables.components.AddTableDialog
import me.sergidalmau.cheflink.ui.screens.tables.components.TableCard
import me.sergidalmau.cheflink.ui.util.ComponentSize
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.dragScroll

@Composable
fun TablesScreen(
    tables: List<Table>,
    orders: List<Order>,
    isEditMode: Boolean,
    componentSize: ComponentSize,
    onSelectTable: (Int) -> Unit,
    onCreateTable: (number: Int, capacity: Int) -> Unit,
    onUpdateTable: (number: Int, capacity: Int) -> Unit,
    onDeleteTable: (number: Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
    val strings = LocalChefLinkStrings.current
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isEditMode) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
                shape = shapes.large
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        strings.editModeActive,
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimaryContainer
                    )
                    Button(onClick = { showAddDialog = true }, colors = ButtonDefaults.filledTonalButtonColors()) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Text(strings.addTable, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }

        BoxWithConstraints {
            val isPC = maxWidth > 800.dp
            val gridState = rememberLazyGridState()
            val minGridSize = when (componentSize) {
                ComponentSize.SMALL -> if (isPC) 120.dp else 80.dp
                ComponentSize.MEDIUM -> if (isPC) 180.dp else 120.dp
                ComponentSize.LARGE -> if (isPC) 240.dp else 160.dp
            }
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = minGridSize),
                contentPadding = PaddingValues(if (componentSize == ComponentSize.SMALL) 6.dp else 8.dp),
                horizontalArrangement = Arrangement.spacedBy(if (componentSize == ComponentSize.SMALL) 6.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (componentSize == ComponentSize.SMALL) 6.dp else 16.dp),
                modifier = Modifier.dragScroll(gridState)
            ) {
                items(tables.sortedBy { it.number }, key = { it.number }) { table ->
                    TableCard(
                        table = table,
                        isOccupied = orders.any { it.tableNumber == table.number },
                        isEditMode = isEditMode,
                        componentSize = componentSize,
                        onClick = { if (!isEditMode) onSelectTable(table.number) },
                        onEdit = { newCapacity -> onUpdateTable(table.number, newCapacity) },
                        onDelete = { onDeleteTable(table.number) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddTableDialog(
            tables = tables,
            onConfirm = { n, c -> onCreateTable(n, c) },
            onDismiss = { showAddDialog = false }
        )
    }
}

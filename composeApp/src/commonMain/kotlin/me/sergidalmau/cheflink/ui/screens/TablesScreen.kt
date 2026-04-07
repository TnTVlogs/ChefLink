package me.sergidalmau.cheflink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.Order
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.ui.theme.successColor
import me.sergidalmau.cheflink.ui.theme.warningColor
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
    val strings = LocalChefLinkStrings.current
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isEditMode) {
            // Edit mode header
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.editModeActive,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Text(strings.addTable, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }

        BoxWithConstraints {
            val isPC = maxWidth > 800.dp
            val gridState = rememberLazyGridState()
            
            val minGridSize = when(componentSize) {
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
                    val isOccupied = orders.any { it.tableNumber == table.number }
                    TableCard(
                        table = table,
                        isOccupied = isOccupied,
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

    // Add Table Dialog
    if (showAddDialog) {
        var newNumber by remember { mutableStateOf("") }
        var newCapacity by remember { mutableStateOf("4") }
        val numberError = newNumber.toIntOrNull() == null && newNumber.isNotEmpty()
        val alreadyExists = tables.any { it.number == newNumber.toIntOrNull() }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(strings.addTable) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newNumber,
                        onValueChange = { newNumber = it },
                        label = { Text(strings.tableNumber) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = numberError || alreadyExists,
                        supportingText = {
                            if (alreadyExists) Text(strings.tableAlreadyExists)
                            else if (numberError) Text("Invalid number")
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    OutlinedTextField(
                        value = newCapacity,
                        onValueChange = { newCapacity = it },
                        label = { Text(strings.tableCapacity) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = MaterialTheme.shapes.medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val n = newNumber.toIntOrNull() ?: return@Button
                        val c = newCapacity.toIntOrNull() ?: 4
                        if (!alreadyExists) {
                            onCreateTable(n, c)
                            showAddDialog = false
                        }
                    },
                    enabled = newNumber.toIntOrNull() != null && !alreadyExists
                ) { Text(strings.addTable) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text(strings.cancel) }
            }
        )
    }
}

@Composable
fun TableCard(
    table: Table,
    isOccupied: Boolean,
    isEditMode: Boolean,
    componentSize: ComponentSize,
    onClick: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    val success = successColor()
    val warning = warningColor()
    val bgColor = if (isOccupied) warning.copy(alpha = 0.15f) else success.copy(alpha = 0.15f)
    val statusColor = if (isOccupied) warning else success
    val statusText = if (isOccupied) strings.occupied else strings.free

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val iconSize = when(componentSize) {
        ComponentSize.SMALL -> 20.dp
        ComponentSize.MEDIUM -> 48.dp
        ComponentSize.LARGE -> 64.dp
    }
    val cardPadding = when(componentSize) {
        ComponentSize.SMALL -> 6.dp
        ComponentSize.MEDIUM -> 24.dp
        ComponentSize.LARGE -> 32.dp
    }
    val titleStyle = when(componentSize) {
        ComponentSize.SMALL -> MaterialTheme.typography.titleSmall
        ComponentSize.MEDIUM -> MaterialTheme.typography.titleLarge
        ComponentSize.LARGE -> MaterialTheme.typography.headlineSmall
    }
    
    val cardShape = if (componentSize == ComponentSize.SMALL) RoundedCornerShape(8.dp) else MaterialTheme.shapes.medium

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isEditMode, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = cardShape
    ) {
        Box {
            Column(
                modifier = Modifier
                    .background(bgColor)
                    .padding(cardPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.TableBar,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = statusColor
                )

                Spacer(modifier = Modifier.height(if (componentSize == ComponentSize.SMALL) 4.dp else 12.dp))

                Text(
                    text = "${strings.table} ${table.number}",
                    style = titleStyle,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                if (componentSize != ComponentSize.SMALL) {
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Box(
                    modifier = Modifier
                        .background(statusColor, shape = RoundedCornerShape(4.dp))
                        .padding(
                            horizontal = if (componentSize == ComponentSize.SMALL) 4.dp else 12.dp,
                            vertical = if (componentSize == ComponentSize.SMALL) 2.dp else 6.dp
                        )
                        .widthIn(min = if (componentSize == ComponentSize.SMALL) 50.dp else 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusText,
                        color = Color.White,
                        style = if (componentSize == ComponentSize.SMALL) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (isEditMode) {
                    Spacer(modifier = Modifier.height(if (componentSize == ComponentSize.SMALL) 6.dp else 12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(if (componentSize == ComponentSize.SMALL) 2.dp else 8.dp)) {
                        IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 28.dp else 48.dp)) {
                            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 16.dp else 20.dp))
                        }
                        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 28.dp else 48.dp)) {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 16.dp else 20.dp))
                        }
                    }
                }
            }
        }
    }

    // Delete confirm dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(strings.deleteTable) },
            text = { 
                if (isOccupied) {
                    Text(
                        text = strings.cannotDeleteTableWithOrders,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(strings.deleteTableConfirm(table.number))
                }
            },
            confirmButton = {
                if (!isOccupied) {
                    Button(
                        onClick = { onDelete(); showDeleteConfirm = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text(strings.delete) }
                } else {
                    Button(onClick = { showDeleteConfirm = false }) { Text(strings.close) }
                }
            },
            dismissButton = {
                if (!isOccupied) {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text(strings.cancel) }
                }
            }
        )
    }

    // Edit dialog (only capacity)
    if (showEditDialog) {
        var editCapacity by remember { mutableStateOf(table.capacity.toString()) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("${strings.table} ${table.number}") },
            text = {
                OutlinedTextField(
                    value = editCapacity,
                    onValueChange = { editCapacity = it },
                    label = { Text(strings.tableCapacity) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = MaterialTheme.shapes.medium
                )
            },
            confirmButton = {
                Button(onClick = {
                    val c = editCapacity.toIntOrNull() ?: table.capacity
                    onEdit(c)
                    showEditDialog = false
                }) { Text(strings.save) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text(strings.cancel) }
            }
        )
    }
}

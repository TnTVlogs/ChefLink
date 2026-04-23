package me.sergidalmau.cheflink.ui.screens.tables.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.ui.theme.successColor
import me.sergidalmau.cheflink.ui.theme.warningColor
import me.sergidalmau.cheflink.ui.util.ComponentSize
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

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
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
    val strings = LocalChefLinkStrings.current
    val success = successColor()
    val warning = warningColor()
    val bgColor = if (isOccupied) warning.copy(alpha = 0.15f) else success.copy(alpha = 0.15f)
    val statusColor = if (isOccupied) warning else success
    val statusText = if (isOccupied) strings.occupied else strings.free

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val iconSize = when (componentSize) {
        ComponentSize.SMALL -> 20.dp; ComponentSize.MEDIUM -> 48.dp; ComponentSize.LARGE -> 64.dp
    }
    val cardPadding = when (componentSize) {
        ComponentSize.SMALL -> 6.dp; ComponentSize.MEDIUM -> 24.dp; ComponentSize.LARGE -> 32.dp
    }
    val titleStyle = when (componentSize) {
        ComponentSize.SMALL -> typography.titleSmall; ComponentSize.MEDIUM -> typography.titleLarge; ComponentSize.LARGE -> typography.headlineSmall
    }
    val cardShape = if (componentSize == ComponentSize.SMALL) RoundedCornerShape(8.dp) else shapes.medium

    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !isEditMode, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = cardShape
    ) {
        Box {
            Column(
                modifier = Modifier.background(bgColor).padding(cardPadding).fillMaxWidth(),
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
                    "${strings.table} ${table.number}",
                    style = titleStyle,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(if (componentSize == ComponentSize.SMALL) 6.dp else 16.dp))
                Box(
                    modifier = Modifier.background(statusColor, shape = RoundedCornerShape(4.dp))
                        .padding(
                            horizontal = if (componentSize == ComponentSize.SMALL) 4.dp else 12.dp,
                            vertical = if (componentSize == ComponentSize.SMALL) 2.dp else 6.dp
                        )
                        .widthIn(min = if (componentSize == ComponentSize.SMALL) 50.dp else 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        statusText,
                        color = Color.White,
                        style = if (componentSize == ComponentSize.SMALL) typography.labelSmall else typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                if (isEditMode) {
                    Spacer(modifier = Modifier.height(if (componentSize == ComponentSize.SMALL) 6.dp else 12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            if (componentSize == ComponentSize.SMALL)
                                2.dp
                            else
                                8.dp
                        )
                    ) {
                        IconButton(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 28.dp else 48.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 16.dp else 20.dp)
                            )
                        }
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 28.dp else 48.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = colorScheme.error,
                                modifier = Modifier.size(if (componentSize == ComponentSize.SMALL) 16.dp else 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(strings.deleteTable) },
            text = {
                if (isOccupied) Text(
                    strings.cannotDeleteTableWithOrders,
                    color = colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                else Text(strings.deleteTableConfirm(table.number))
            },
            confirmButton = {
                if (!isOccupied) Button(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) { Text(strings.delete) }
                else Button(onClick = { showDeleteConfirm = false }) { Text(strings.close) }
            },
            dismissButton = {
                if (!isOccupied) TextButton(onClick = {
                    showDeleteConfirm = false
                }) { Text(strings.cancel) }
            }
        )
    }

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
                    shape = shapes.medium
                )
            },
            confirmButton = {
                Button(onClick = {
                    onEdit(editCapacity.toIntOrNull() ?: table.capacity); showEditDialog = false
                }) { Text(strings.save) }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text(strings.cancel) } }
        )
    }
}

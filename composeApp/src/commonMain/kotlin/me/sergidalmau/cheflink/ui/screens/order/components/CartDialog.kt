package me.sergidalmau.cheflink.ui.screens.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.OrderItem
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.dragScroll
import me.sergidalmau.cheflink.ui.util.formatPrice

@Composable
fun CartDialog(
    cart: SnapshotStateList<OrderItem>,
    orderNotes: String,
    onOrderNotesChange: (String) -> Unit,
    onAddToCart: (OrderItem) -> Unit,
    onRemoveFromCart: (Int) -> Unit,
    onDeleteFromCart: (Int) -> Unit,
    onEditNote: (Int, String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.orderSummary) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (cart.isEmpty()) {
                    Text(strings.emptyOrder, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    val listState = rememberLazyListState()
                    LazyColumn(state = listState, modifier = Modifier.weight(1f, fill = false).dragScroll(listState)) {
                        itemsIndexed(cart) { index, item ->
                            CartItemRow(
                                item = item,
                                onIncrement = { onAddToCart(OrderItem(item.product, 1, item.notes)) },
                                onDecrement = { onRemoveFromCart(index) },
                                onDelete = { onDeleteFromCart(index) },
                                onEditNote = { onEditNote(index, item.notes ?: "") }
                            )
                            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = orderNotes,
                        onValueChange = onOrderNotesChange,
                        label = { Text(strings.generalNotes) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${strings.total}:", style = typography.titleLarge)
                        Text(
                            "${cart.sumOf { it.product.price * it.quantity }.formatPrice()}€",
                            style = typography.titleLarge,
                            color = colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSend, enabled = cart.isNotEmpty()) {
                Icon(Icons.AutoMirrored.Filled.Send, null, Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.send)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.close) } }
    )
}

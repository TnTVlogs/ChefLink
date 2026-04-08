package me.sergidalmau.cheflink.ui.screens.orderslist.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun DeleteOrderDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.deleteOrder) },
        text = { Text(strings.deleteOrderConfirm) },
        confirmButton = {
            TextButton(onClick = onConfirm, colors = textButtonColors(contentColor = colorScheme.error)) {
                Text(strings.delete)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } }
    )
}

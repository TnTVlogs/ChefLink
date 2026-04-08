package me.sergidalmau.cheflink.ui.screens.order.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun ProductNoteDialog(
    product: Product,
    currentNote: String,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${strings.addNoteTo} ${product.name}") },
        text = {
            OutlinedTextField(
                value = currentNote,
                onValueChange = onNoteChange,
                label = { Text(strings.note) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { Button(onClick = onConfirm) { Text(strings.addNote) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } }
    )
}

@Composable
fun CartItemNoteDialog(
    currentNote: String,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.note) },
        text = {
            OutlinedTextField(
                value = currentNote,
                onValueChange = onNoteChange,
                label = { Text(strings.note) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { Button(onClick = onConfirm) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } }
    )
}

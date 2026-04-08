package me.sergidalmau.cheflink.ui.screens.tables.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.Table
import me.sergidalmau.cheflink.ui.screens.components.ChefLinkTextField
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun AddTableDialog(
    tables: List<Table>,
    onConfirm: (number: Int, capacity: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    var newNumber by remember { mutableStateOf("") }
    var newCapacity by remember { mutableStateOf("4") }
    val numberError = newNumber.toIntOrNull() == null && newNumber.isNotEmpty()
    val alreadyExists = tables.any { it.number == newNumber.toIntOrNull() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addTable) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ChefLinkTextField(
                    value = newNumber,
                    onValueChange = { newNumber = it },
                    label = strings.tableNumber,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = numberError || alreadyExists,
                    supportingText = {
                        if (alreadyExists) Text(strings.tableAlreadyExists)
                        else if (numberError) Text("Invalid number")
                    }
                )
                ChefLinkTextField(
                    value = newCapacity,
                    onValueChange = { newCapacity = it },
                    label = strings.tableCapacity,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val n = newNumber.toIntOrNull() ?: return@Button
                    val c = newCapacity.toIntOrNull() ?: 4
                    if (!alreadyExists) {
                        onConfirm(n, c); onDismiss()
                    }
                },
                enabled = newNumber.toIntOrNull() != null && !alreadyExists
            ) { Text(strings.addTable) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } }
    )
}

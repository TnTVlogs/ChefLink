package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import me.sergidalmau.cheflink.ui.screens.components.ChefLinkTextField
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun ChangePasswordDialog(
    onConfirm: (oldPass: String, newPass: String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.changePassword) },
        text = {
            Column {
                ChefLinkTextField(value = oldPass, onValueChange = { oldPass = it }, label = strings.password)
                ChefLinkTextField(value = newPass, onValueChange = { newPass = it }, label = strings.password)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(oldPass, newPass); onDismiss() }) { Text(strings.changePassword) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel) } }
    )
}

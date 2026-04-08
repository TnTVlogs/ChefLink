package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.ui.screens.components.ChefLinkTextField
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun RegisterUserDialog(
    registrationMessage: String?,
    onRegister: (String, String, String, String, String, UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newFirstName by remember { mutableStateOf("") }
    var newLastName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.Cambrer) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Nou Usuari") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                if (registrationMessage != null) {
                    Text(
                        text = registrationMessage,
                        color = if (registrationMessage.contains("Error")) colorScheme.error else colorScheme.primary,
                        style = typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                ChefLinkTextField(value = newUsername, onValueChange = { newUsername = it }, label = strings.username)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                ChefLinkTextField(value = newPassword, onValueChange = { newPassword = it }, label = strings.password)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                ChefLinkTextField(
                    value = newFirstName,
                    onValueChange = { newFirstName = it },
                    label = strings.firstName
                )
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                ChefLinkTextField(value = newLastName, onValueChange = { newLastName = it }, label = strings.lastName)
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                ChefLinkTextField(value = newEmail, onValueChange = { newEmail = it }, label = strings.email)
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text("${strings.userRole}:", style = typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRole == UserRole.Cambrer,
                        onClick = { selectedRole = UserRole.Cambrer })
                    Text(strings.roleWaiter, modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = selectedRole == UserRole.Admin, onClick = { selectedRole = UserRole.Admin })
                    Text(strings.roleAdmin, modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newUsername.isNotBlank() && newPassword.isNotBlank()) {
                    onRegister(newUsername, newPassword, newFirstName, newLastName, newEmail, selectedRole)
                }
            }) { Text(strings.register) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.close) } }
    )
}

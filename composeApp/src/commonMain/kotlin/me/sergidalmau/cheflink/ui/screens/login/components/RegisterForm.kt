package me.sergidalmau.cheflink.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.ui.screens.components.ChefLinkTextField
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun RegisterForm(
    firstNameValue: String,
    onFirstNameChange: (String) -> Unit,
    lastNameValue: String,
    onLastNameChange: (String) -> Unit,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit
) {
    val strings = LocalChefLinkStrings.current
    val typography = MaterialTheme.typography

    ChefLinkTextField(
        value = firstNameValue,
        onValueChange = onFirstNameChange,
        label = strings.firstName
    )
    Spacer(modifier = Modifier.height(8.dp))
    ChefLinkTextField(
        value = lastNameValue,
        onValueChange = onLastNameChange,
        label = strings.lastName
    )
    Spacer(modifier = Modifier.height(8.dp))
    ChefLinkTextField(
        value = emailValue,
        onValueChange = onEmailChange,
        label = strings.email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    Spacer(modifier = Modifier.height(16.dp))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("${strings.userRole}:", style = typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedRole == UserRole.Cambrer,
                onClick = { onRoleSelected(UserRole.Cambrer) }
            )
            Text(strings.roleWaiter)
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = selectedRole == UserRole.Admin,
                onClick = { onRoleSelected(UserRole.Admin) }
            )
            Text(strings.roleAdmin)
        }
    }
}

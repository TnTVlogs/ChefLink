package me.sergidalmau.cheflink.ui.screens.login.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.ui.screens.components.ChefLinkTextField
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun LoginForm(
    usernameValue: String,
    onUsernameChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit
) {
    val strings = LocalChefLinkStrings.current

    ChefLinkTextField(
        value = usernameValue,
        onValueChange = onUsernameChange,
        label = strings.username
    )

    Spacer(modifier = Modifier.height(16.dp))

    ChefLinkTextField(
        value = passwordValue,
        onValueChange = onPasswordChange,
        label = strings.password,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                Icon(
                    imageVector = image,
                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                )
            }
        }
    )
}

package me.sergidalmau.cheflink.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun ForceChangePasswordScreen(
    onChangePassword: (old: String, new: String) -> Unit,
    message: String?
) {
    val strings = LocalChefLinkStrings.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val errorToShow = localError ?: if (message?.startsWith("Error") == true) message else null

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Card(
                modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        strings.forceChangePasswordTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        strings.forceChangePasswordDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it; localError = null },
                        label = { Text(strings.currentPassword) },
                        placeholder = { Text("admin") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; localError = null },
                        label = { Text(strings.newPasswordLabel) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; localError = null },
                        label = { Text(strings.confirmNewPassword) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword
                    )

                    if (errorToShow != null) {
                        Text(
                            errorToShow,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            when {
                                oldPassword.isBlank() -> localError = strings.enterCurrentPassword
                                newPassword.length < 4 -> localError = strings.passwordTooShort
                                newPassword != confirmPassword -> localError = strings.passwordsDoNotMatch
                                else -> onChangePassword(oldPassword, newPassword)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.changePasswordAction)
                    }
                }
            }
        }
    }
}

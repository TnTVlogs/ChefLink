package me.sergidalmau.cheflink.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.ui.screens.login.components.FeedbackMessage
import me.sergidalmau.cheflink.ui.screens.login.components.LoginForm
import me.sergidalmau.cheflink.ui.util.dragScroll
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String, String, UserRole) -> Unit = { _, _, _, _, _, _ -> },
    errorMessage: String? = null,
    registrationMessage: String? = null,
    registrationSuccess: Boolean = false,
    onResetRegistrationStatus: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
    val strings = LocalChefLinkStrings.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().background(colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorScheme.primary.copy(alpha = 0.1f), colorScheme.surface)
                )
            )
        )

        Card(
            modifier = Modifier.width(450.dp).padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.padding(vertical = 48.dp, horizontal = 32.dp).verticalScroll(scrollState)
                    .dragScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(80.dp).background(colorScheme.primaryContainer, shape = shapes.large),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        "Logo",
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    strings.appTitle,
                    style = typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.primary
                )
                Text(
                    strings.login,
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )

                FeedbackMessage(errorMessage, registrationMessage, registrationSuccess)
                Spacer(modifier = Modifier.height(24.dp))

                LoginForm(
                    username, { username = it },
                    password, { password = it },
                    passwordVisible, { passwordVisible = it }
                )

                Spacer(modifier = Modifier.height(24.dp))
                if (validationError.isNotEmpty()) {
                    Text(
                        validationError,
                        color = colorScheme.error,
                        style = typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            onLogin(username, password); validationError = ""
                        } else validationError = "Si us plau, introdueix usuari i contrasenya"
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp), shape = shapes.large
                ) {
                    Text(strings.login, style = typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Els nous usuaris els ha de crear un administrador des de configuracio.",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

            }
        }
    }
}

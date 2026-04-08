package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.User
import me.sergidalmau.cheflink.domain.models.UserRole
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun UserProfileCard(
    user: User,
    onChangePassword: () -> Unit,
    onRegisterUser: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes
    val strings = LocalChefLinkStrings.current

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = shapes.large
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).clip(shapes.medium).background(colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(32.dp),
                        tint = colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    val fullName = if (user.firstName.isNotEmpty() || user.lastName.isNotEmpty()) {
                        "${user.firstName} ${user.lastName}".trim()
                    } else {
                        user.username
                    }
                    Text(fullName, style = typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text("@${user.username}", style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AssistChip(
                onClick = {},
                label = { Text(if (user.role == UserRole.Admin) strings.roleAdmin else strings.roleWaiter) },
                shape = shapes.small,
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = colorScheme.secondaryContainer,
                    labelColor = colorScheme.onSecondaryContainer
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onChangePassword,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Text(strings.changePassword, textAlign = TextAlign.Center, style = typography.labelLarge)
                }
                if (user.role == UserRole.Admin) {
                    Button(
                        onClick = onRegisterUser,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.surface,
                            contentColor = colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Text(strings.newUser, textAlign = TextAlign.Center, style = typography.labelLarge)
                    }
                }
            }
        }
    }
}

package me.sergidalmau.cheflink.ui.screens.login.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.ui.theme.successColor

@Composable
fun FeedbackMessage(
    errorMessage: String?,
    registrationMessage: String?,
    registrationSuccess: Boolean
) {
    if (errorMessage == null && registrationMessage == null) return

    val msg = registrationMessage ?: errorMessage
    val isSuccess = registrationSuccess && msg?.contains("correctament") == true
    val colorScheme = MaterialTheme.colorScheme
    val success = successColor()
    val bgColor = if (isSuccess) success else colorScheme.error
    val textColor = bgColor

    Spacer(modifier = Modifier.height(16.dp))
    Card(
        colors = CardDefaults.cardColors(
            containerColor = bgColor.copy(alpha = 0.1f)
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = msg ?: "",
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        )
    }
}

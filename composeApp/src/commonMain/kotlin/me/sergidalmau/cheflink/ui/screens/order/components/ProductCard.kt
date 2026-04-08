package me.sergidalmau.cheflink.ui.screens.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import me.sergidalmau.cheflink.ui.util.formatPrice

@Composable
fun ProductCard(
    product: Product,
    onAdd: () -> Unit,
    onAddWithNote: () -> Unit,
    cartQuantity: Int
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    val strings = LocalChefLinkStrings.current
    val isSelected = cartQuantity > 0
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shapes.large)
            .clickable(enabled = product.isAvailable, onClick = onAdd),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        shape = shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when {
                !product.isAvailable -> colorScheme.surfaceVariant.copy(alpha = 0.4f)
                isSelected -> colorScheme.primaryContainer
                else -> colorScheme.surface
            },
            contentColor = when {
                !product.isAvailable -> colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                isSelected -> colorScheme.onPrimaryContainer
                else -> colorScheme.onSurface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )
                    if (isSelected) {
                        Badge(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ) {
                            Text(cartQuantity.toString(), modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.description ?: strings.noDescription,
                    style = typography.bodySmall,
                    color = if (isSelected) colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else colorScheme.onSurfaceVariant,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.price.formatPrice()}€",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (product.isAvailable) colorScheme.primary
                    else colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )

                FilledTonalButton(
                    onClick = onAddWithNote,
                    enabled = product.isAvailable,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = shapes.medium,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isSelected) colorScheme.primary
                        else colorScheme.secondaryContainer,
                        contentColor = if (isSelected) colorScheme.onPrimary
                        else colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Note, null, Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.note, style = typography.labelSmall)
                }
            }
        }
    }
}

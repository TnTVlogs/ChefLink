package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings
import kotlin.text.ifEmpty

@Composable
fun ArticleEditDialog(
    product: Product?,
    onClose: () -> Unit,
    onSave: (String, ProductCategory, Double, String?, Boolean) -> Unit
) {
    val strings = LocalChefLinkStrings.current
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: ProductCategory.Primers) }
    var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "0.0") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var isAvailable by remember { mutableStateOf(product?.isAvailable ?: true) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(if (product == null) strings.addArticle else strings.editArticle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.articleName) },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = when (category) {
                            ProductCategory.Primers -> strings.categoryPrimers
                            ProductCategory.Segons -> strings.categorySegons
                            ProductCategory.Postres -> strings.categoryPostres
                            ProductCategory.Begudes -> strings.categoryBegudes
                            ProductCategory.Menus -> strings.categoryMenus
                        },
                        onValueChange = {},
                        label = { Text(strings.articleCategory) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { categoryMenuExpanded = true }) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) {
                        ProductCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (cat) {
                                            ProductCategory.Primers -> strings.categoryPrimers
                                            ProductCategory.Segons -> strings.categorySegons
                                            ProductCategory.Postres -> strings.categoryPostres
                                            ProductCategory.Begudes -> strings.categoryBegudes
                                            ProductCategory.Menus -> strings.categoryMenus
                                        }
                                    )
                                },
                                onClick = { category = cat; categoryMenuExpanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text(strings.articlePrice) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(strings.articleDescription) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.articleAvailable)
                    Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    name,
                    category,
                    priceStr.toDoubleOrNull() ?: 0.0,
                    description.ifEmpty { null },
                    isAvailable
                )
            }) {
                Text(strings.save)
            }
        },
        dismissButton = { TextButton(onClick = onClose) { Text(strings.cancel) } }
    )
}

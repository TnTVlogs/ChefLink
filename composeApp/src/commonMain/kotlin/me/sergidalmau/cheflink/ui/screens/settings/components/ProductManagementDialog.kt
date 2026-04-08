package me.sergidalmau.cheflink.ui.screens.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@Composable
fun ProductManagementDialog(
    products: List<Product>,
    onClose: () -> Unit,
    onCreate: (String, ProductCategory, Double, String?, Boolean) -> Unit,
    onUpdate: (String, String, ProductCategory, Double, String?, Boolean) -> Unit,
    onDelete: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val strings = LocalChefLinkStrings.current
    var showEditDialog by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.category.name.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(strings.articles)
                IconButton(onClick = { showEditDialog = Product("", "", ProductCategory.Primers, 0.0) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
        text = {
            Column(modifier = Modifier.height(400.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    placeholder = { Text(strings.filterByTable) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredProducts.size) { index ->
                        val product = filteredProducts[index]
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showEditDialog = product }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, style = typography.bodyLarge)
                                Text(
                                    text = when (product.category) {
                                        ProductCategory.Primers -> strings.categoryPrimers
                                        ProductCategory.Segons -> strings.categorySegons
                                        ProductCategory.Postres -> strings.categoryPostres
                                        ProductCategory.Begudes -> strings.categoryBegudes
                                        ProductCategory.Menus -> strings.categoryMenus
                                    },
                                    style = typography.bodySmall,
                                    color = if (product.isAvailable) colorScheme.onSurfaceVariant else colorScheme.error
                                )
                                if (!product.isAvailable) {
                                    Text(strings.outOfStock, style = typography.labelSmall, color = colorScheme.error)
                                }
                            }
                            Text("${product.price}€", style = typography.bodyMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { onDelete(product.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = colorScheme.error)
                            }
                        }
                        if (index < filteredProducts.size - 1) HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onClose) { Text(strings.close) } }
    )

    if (showEditDialog != null) {
        val editingProduct = showEditDialog!!
        ArticleEditDialog(
            product = if (editingProduct.id.isEmpty()) null else editingProduct,
            onClose = { showEditDialog = null },
            onSave = { n, c, p, d, a ->
                if (editingProduct.id.isEmpty()) onCreate(n, c, p, d, a)
                else onUpdate(editingProduct.id, n, c, p, d, a)
                showEditDialog = null
            }
        )
    }
}

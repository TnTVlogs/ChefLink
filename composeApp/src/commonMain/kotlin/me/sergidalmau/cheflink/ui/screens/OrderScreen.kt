package me.sergidalmau.cheflink.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.OrderItem
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.ui.util.dragScroll
import me.sergidalmau.cheflink.ui.util.formatPrice
import me.sergidalmau.cheflink.ui.util.LocalChefLinkStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    tableNumber: Int,
    products: List<Product>,
    onSendOrder: (List<OrderItem>, String) -> Unit,
    onBack: () -> Unit,
    initialItems: List<OrderItem> = emptyList(),
    initialNotes: String = ""
) {
    var selectedCategory by remember { mutableStateOf(ProductCategory.Primers) }
    val cart = remember {
        androidx.compose.runtime.mutableStateListOf<OrderItem>().apply {
            addAll(initialItems.map { it.copy() })
        }
    }
    var orderNotes by remember { mutableStateOf(initialNotes) }
    var cartOpen by remember { mutableStateOf(false) }

    // Dialog states
    var noteDialogOpen by remember { mutableStateOf(false) }
    var currentProduct by remember { mutableStateOf<Product?>(null) }
    var currentNote by remember { mutableStateOf("") }

    // Cart Item Note Dialog
    var cartItemNoteDialog by remember { mutableStateOf(false) }
    var currentCartItemIndex by remember { mutableStateOf<Int?>(null) }
    
    val strings = LocalChefLinkStrings.current
    val filteredProducts = products.filter { it.category == selectedCategory }
    val totalItems = cart.sumOf { it.quantity }

    fun addToCart(product: Product, note: String? = null) {
        val existingIndex = cart.indexOfFirst { it.product.id == product.id && it.notes == note }
        if (existingIndex >= 0) {
            cart[existingIndex] = cart[existingIndex].copy(quantity = cart[existingIndex].quantity + 1)
        } else {
            cart.add(OrderItem(product, 1, note))
        }
    }

    fun removeFromCart(index: Int) {
        if (cart[index].quantity > 1) {
            cart[index] = cart[index].copy(quantity = cart[index].quantity - 1)
        } else {
            cart.removeAt(index)
        }
    }

    fun deleteFromCart(index: Int) {
        cart.removeAt(index)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.small // Rectangular-ish
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.orderTitle} - ${strings.table} $tableNumber",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    BadgedBox(
                        badge = {
                            if (totalItems > 0) {
                                Badge { Text(totalItems.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { cartOpen = true }) {
                            Icon(Icons.Default.ShoppingCart, "Cart")
                        }
                    }
                }
            }

            // Tabs
            val selectedTabIndex = ProductCategory.entries.indexOf(selectedCategory)
            SecondaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = true)
                    )
                },
                divider = {
                    HorizontalDivider()
                }
            ) {
                ProductCategory.entries.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category.name) }
                    )
                }
            }

            // Product Grid
            val gridState = rememberLazyGridState()
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f).dragScroll(gridState)
            ) {
                items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onAdd = { addToCart(product) },
                            onAddWithNote = {
                                currentProduct = product
                                currentNote = ""
                                noteDialogOpen = true
                            },
                            cartQuantity = cart.filter { it.product.id == product.id }.sumOf { it.quantity }
                        )
                }
            }
        }
    }

    // Cart Dialog (FullScreen-ish)
    if (cartOpen) {
        AlertDialog(
            onDismissRequest = { cartOpen = false },
            title = { Text(strings.orderSummary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (cart.isEmpty()) {
                        Text(
                            strings.emptyOrder,
                            modifier = Modifier.padding(vertical = 32.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        val listState = rememberLazyListState()
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f, fill = false).dragScroll(listState)
                        ) {
                            itemsIndexed(cart) { index, item ->
                                CartItemRow(
                                    item = item,
                                    onIncrement = { addToCart(item.product, item.notes) },
                                    onDecrement = { removeFromCart(index) },
                                    onDelete = { deleteFromCart(index) },
                                    onEditNote = {
                                        currentCartItemIndex = index
                                        currentNote = item.notes ?: ""
                                        cartItemNoteDialog = true
                                    }
                                )
                                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = orderNotes,
                            onValueChange = { orderNotes = it },
                            label = { Text(strings.generalNotes) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${strings.total}:", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "${cart.sumOf { it.product.price * it.quantity }.formatPrice()}€",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSendOrder(cart, orderNotes)
                    },
                    enabled = cart.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.send)
                }
            },
            dismissButton = {
                TextButton(onClick = { cartOpen = false }) {
                    Text(strings.close)
                }
            }
        )
    }

    // Add Note Dialog
    if (noteDialogOpen && currentProduct != null) {
        AlertDialog(
            onDismissRequest = { noteDialogOpen = false },
            title = { Text("${strings.addNoteTo} ${currentProduct?.name}") },
            text = {
                OutlinedTextField(
                    value = currentNote,
                    onValueChange = { currentNote = it },
                    label = { Text(strings.note) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    addToCart(currentProduct!!, currentNote.ifEmpty { null })
                    noteDialogOpen = false
                    currentProduct = null
                }) {
                    Text(strings.addNote)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteDialogOpen = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Edit Cart Item Note Dialog
    if (cartItemNoteDialog && currentCartItemIndex != null) {
        AlertDialog(
            onDismissRequest = { cartItemNoteDialog = false },
            title = { Text(strings.note) },
            text = {
                OutlinedTextField(
                    value = currentNote,
                    onValueChange = { currentNote = it },
                    label = { Text(strings.note) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    val index = currentCartItemIndex!!
                    if (index in cart.indices) {
                        cart[index] = cart[index].copy(notes = currentNote.ifEmpty { null })
                    }
                    cartItemNoteDialog = false
                    currentCartItemIndex = null
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { cartItemNoteDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAdd: () -> Unit,
    onAddWithNote: () -> Unit,
    cartQuantity: Int
) {
    val strings = LocalChefLinkStrings.current
    val isSelected = cartQuantity > 0
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = product.isAvailable, onClick = onAdd),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when {
                !product.isAvailable -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            },
            contentColor = when {
                !product.isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> MaterialTheme.colorScheme.onSurface
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )
                    if (isSelected) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(cartQuantity.toString(), modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.description ?: strings.noDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    minLines = 2,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.price.formatPrice()}€",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (product.isAvailable) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )

                FilledTonalButton(
                    onClick = onAddWithNote,
                    enabled = product.isAvailable,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                      else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Note, null, Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.note, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: OrderItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit,
    onEditNote: () -> Unit
) {
    val strings = LocalChefLinkStrings.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "${item.product.price.formatPrice()}€ x ${item.quantity} = ${(item.product.price * item.quantity).formatPrice()}€",
                style = MaterialTheme.typography.bodyMedium
            )
            if (!item.notes.isNullOrEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onEditNote)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Note,
                        null,
                        Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        item.notes!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                TextButton(
                    onClick = onEditNote,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text(strings.addNote, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Remove, null, Modifier.size(16.dp))
            }
            Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, null, Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

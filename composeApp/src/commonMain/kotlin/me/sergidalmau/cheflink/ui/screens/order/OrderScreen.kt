package me.sergidalmau.cheflink.ui.screens.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import me.sergidalmau.cheflink.domain.models.OrderItem
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.ui.screens.order.components.CartDialog
import me.sergidalmau.cheflink.ui.screens.order.components.CartItemNoteDialog
import me.sergidalmau.cheflink.ui.screens.order.components.OrderHeader
import me.sergidalmau.cheflink.ui.screens.order.components.ProductNoteDialog
import me.sergidalmau.cheflink.ui.screens.order.components.ProductsGrid

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
        mutableStateListOf<OrderItem>().apply { addAll(initialItems.map { it.copy() }) }
    }
    var orderNotes by remember { mutableStateOf(initialNotes) }
    var cartOpen by remember { mutableStateOf(false) }
    var noteDialogOpen by remember { mutableStateOf(false) }
    var currentProduct by remember { mutableStateOf<Product?>(null) }
    var currentNote by remember { mutableStateOf("") }
    var cartItemNoteDialog by remember { mutableStateOf(false) }
    var currentCartItemIndex by remember { mutableStateOf<Int?>(null) }

    fun addToCart(product: Product, note: String? = null) {
        val existingIndex = cart.indexOfFirst { it.product.id == product.id && it.notes == note }
        if (existingIndex >= 0) cart[existingIndex] =
            cart[existingIndex].copy(quantity = cart[existingIndex].quantity + 1)
        else cart.add(OrderItem(product, 1, note))
    }

    fun removeFromCart(index: Int) {
        if (cart[index].quantity > 1) cart[index] = cart[index].copy(quantity = cart[index].quantity - 1)
        else cart.removeAt(index)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            OrderHeader(
                tableNumber = tableNumber,
                totalItems = cart.sumOf { it.quantity },
                onBack = onBack,
                onOpenCart = { cartOpen = true },
                onQuickSend = { onSendOrder(cart.toList(), orderNotes) })
            ProductsGrid(
                products = products,
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                cart = cart,
                onAddToCart = { addToCart(it) },
                onAddWithNote = { currentProduct = it; currentNote = ""; noteDialogOpen = true },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (cartOpen) {
        CartDialog(
            cart = cart,
            orderNotes = orderNotes,
            onOrderNotesChange = { orderNotes = it },
            onAddToCart = { addToCart(it.product, it.notes) },
            onRemoveFromCart = { removeFromCart(it) },
            onDeleteFromCart = { cart.removeAt(it) },
            onEditNote = { index, note -> currentCartItemIndex = index; currentNote = note; cartItemNoteDialog = true },
            onSend = { onSendOrder(cart, orderNotes) },
            onDismiss = { cartOpen = false }
        )
    }

    if (noteDialogOpen && currentProduct != null) {
        ProductNoteDialog(
            product = currentProduct!!,
            currentNote = currentNote,
            onNoteChange = { currentNote = it },
            onConfirm = {
                addToCart(currentProduct!!, currentNote.ifEmpty { null }); noteDialogOpen = false; currentProduct = null
            },
            onDismiss = { noteDialogOpen = false }
        )
    }

    if (cartItemNoteDialog && currentCartItemIndex != null) {
        CartItemNoteDialog(
            currentNote = currentNote,
            onNoteChange = { currentNote = it },
            onConfirm = {
                val index = currentCartItemIndex!!
                if (index in cart.indices) cart[index] = cart[index].copy(notes = currentNote.ifEmpty { null })
                cartItemNoteDialog = false; currentCartItemIndex = null
            },
            onDismiss = { cartItemNoteDialog = false }
        )
    }
}

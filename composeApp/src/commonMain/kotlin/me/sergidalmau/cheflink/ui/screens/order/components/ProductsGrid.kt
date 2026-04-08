package me.sergidalmau.cheflink.ui.screens.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sergidalmau.cheflink.domain.models.OrderItem
import me.sergidalmau.cheflink.domain.models.Product
import me.sergidalmau.cheflink.domain.models.ProductCategory
import me.sergidalmau.cheflink.ui.util.dragScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsGrid(
    products: List<Product>,
    selectedCategory: ProductCategory,
    onCategoryChange: (ProductCategory) -> Unit,
    cart: List<OrderItem>,
    onAddToCart: (Product) -> Unit,
    onAddWithNote: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredProducts = products.filter { it.category == selectedCategory }
    val selectedTabIndex = ProductCategory.entries.indexOf(selectedCategory)

    SecondaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 16.dp,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex,
                    matchContentSize = true
                )
            )
        },
        divider = { HorizontalDivider() }
    ) {
        ProductCategory.entries.forEach { category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategoryChange(category) },
                text = { Text(category.name) })
        }
    }

    val gridState = rememberLazyGridState()
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.dragScroll(gridState)
    ) {
        items(filteredProducts) { product ->
            ProductCard(
                product = product,
                onAdd = { onAddToCart(product) },
                onAddWithNote = { onAddWithNote(product) },
                cartQuantity = cart.filter { it.product.id == product.id }.sumOf { it.quantity }
            )
        }
    }
}

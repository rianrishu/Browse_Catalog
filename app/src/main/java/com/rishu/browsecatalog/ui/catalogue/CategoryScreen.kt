package com.rishu.browsecatalog.ui.catalogue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rishu.browsecatalog.domain.Product
import com.rishu.browsecatalog.domain.inCategory

@Composable
internal fun CategoryScreen(products: List<Product>, category: String, open: (Int) -> Unit) {
    val ranked = remember(products, category) { products.inCategory(category) }
    LazyColumn(contentPadding = PaddingValues(vertical = 12.dp)) {
        item { Text(category.replaceFirstChar { it.titlecase() }, style = MaterialTheme.typography.headlineLarge) }
        if (ranked.topRated.isNotEmpty()) {
            item { Text("Top rated", Modifier.padding(top = 24.dp), style = MaterialTheme.typography.titleLarge) }
            items(ranked.topRated, key = { it.id }) { ProductRow(it, open) }
        }
        if (ranked.additional.isNotEmpty()) item {
            Text("More in this category", Modifier.padding(top = 24.dp), style = MaterialTheme.typography.titleLarge)
        }
        items(ranked.additional, key = { it.id }) { ProductRow(it, open) }
    }
}

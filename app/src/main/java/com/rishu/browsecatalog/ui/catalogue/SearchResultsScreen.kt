package com.rishu.browsecatalog.ui.catalogue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.rishu.browsecatalog.domain.Product
import com.rishu.browsecatalog.domain.matching

@Composable
internal fun SearchResultsScreen(products: List<Product>, term: String, query: String,
    edit: (String) -> Unit, search: () -> Unit, open: (Int) -> Unit) {
    val matches = remember(products, term) { products.matching(term) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
        item { Text("Search results", style = MaterialTheme.typography.headlineLarge) }
        item { ProductSearch(query, edit, search) }
        item { Text(if (matches.isEmpty()) "No matches for “$term”. Try another name or keyword."
            else "${matches.size} ${if (matches.size == 1) "match" else "matches"} for “$term”") }
        items(matches, key = { it.id }) { ProductRow(it, open) }
    }
}

package com.rishu.browsecatalog.ui.catalogue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rishu.browsecatalog.domain.Product

@Composable
internal fun HomeScreen(products: List<Product>, query: String, edit: (String) -> Unit,
    search: () -> Unit, category: (String) -> Unit, open: (Int) -> Unit) {
    val picks = remember(products) { products.filter { it.featured } }
    val categories = remember(products) { products.map { it.category }.distinct() }
    LazyColumn(contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Catalogue", style = MaterialTheme.typography.displaySmall) }
        item { ProductSearch(query, edit, search) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { label -> AssistChip(onClick = { category(label) }, label = { Text(label) }) }
            }
        }
        item { Text("Featured", Modifier.padding(top = 16.dp), style = MaterialTheme.typography.headlineMedium) }
        picks.firstOrNull()?.let { lead ->
            item(key = lead.id) {
                ElevatedCard(onClick = { open(lead.id) }, modifier = Modifier.fillMaxWidth()) {
                    ProductPhoto(lead, Modifier.fillMaxWidth().height(180.dp))
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(lead.category, style = MaterialTheme.typography.labelLarge)
                        Text(lead.title, style = MaterialTheme.typography.titleLarge)
                        ProductPrice(lead)
                    }
                }
            }
        }
        items(picks.drop(1), key = { it.id }) { ProductRow(it, open) }
    }
}

package com.rishu.browsecatalog.ui.catalogue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rishu.browsecatalog.domain.Product

@Composable
internal fun ProductDetailScreen(product: Product, enquire: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)) {
            item { ProductPhoto(product, Modifier.fillMaxWidth().height(240.dp)) }
            item { Text(product.category, color = MaterialTheme.colorScheme.primary) }
            item { Text(product.title, style = MaterialTheme.typography.headlineMedium) }
            item { ProductPrice(product) }
            item { Text("★ ${product.rating} out of 5") }
            item { HorizontalDivider() }
            item { Text("Product details", style = MaterialTheme.typography.titleMedium) }
            item { Text(product.description) }
        }
        Button(onClick = enquire, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text("Enquire") }
    }
}

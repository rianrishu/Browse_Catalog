package com.rishu.browsecatalog.ui.catalogue

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CatalogueScreen(model: CatalogueViewModel) {
    val state by model.state.collectAsStateWithLifecycle()
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf("") }
    var productId by rememberSaveable { mutableStateOf<Int?>(null) }
    var form by rememberSaveable { mutableStateOf(false) }
    val product = state.products.firstOrNull { it.id == productId }
    val nested = form || productId != null || category != null || submitted.isNotEmpty()
    val back = {
        when {
            form -> form = false
            productId != null -> productId = null
            else -> { category = null; submitted = ""; query = "" }
        }
    }
    BackHandler(nested) { if (state.delivery != Delivery.Sending) back() }
    Scaffold { insets ->
        Column(Modifier.padding(insets).fillMaxSize().padding(horizontal = 20.dp)) {
            if (nested) TextButton(onClick = back, enabled = state.delivery != Delivery.Sending) { Text("Back") }
            when {
                state.loading -> CircularProgressIndicator()
                state.loadFailed -> {
                    Text("Couldn’t load the catalogue.")
                    Button(onClick = { model.refresh() }) { Text("Try again") }
                }
                product != null && form -> EnquiryScreen(product, state.delivery, model::enquire) {
                    productId = null
                    form = false
                    category = null
                    submitted = ""
                    query = ""
                }
                product != null -> ProductDetailScreen(product) {
                    model.resetEnquiry()
                    form = true
                }
                submitted.isNotEmpty() -> SearchResultsScreen(state.products, submitted, query,
                    { query = it }, { submitted = query.trim() }, { productId = it })
                category != null -> CategoryScreen(state.products, category.orEmpty()) { productId = it }
                else -> HomeScreen(state.products, query, { query = it }, { submitted = query.trim() },
                    { category = it }, { productId = it })
            }
        }
    }
}

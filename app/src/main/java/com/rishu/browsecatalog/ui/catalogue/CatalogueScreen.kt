package com.rishu.browsecatalog.ui.catalogue

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rishu.browsecatalog.domain.Product
import com.rishu.browsecatalog.domain.inCategory
import com.rishu.browsecatalog.domain.matching
import java.text.NumberFormat
import java.util.Locale

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
                product != null && form -> EnquiryForm(product, state.delivery, model::enquire)
                product != null -> LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { Text(product.category, style = MaterialTheme.typography.labelLarge) }
                    item { Text(product.title, style = MaterialTheme.typography.headlineMedium) }
                    item { Text(product.summary()) }
                    item { Text(product.description) }
                    item { Button(onClick = { model.resetEnquiry(); form = true }) { Text("Enquire") } }
                }
                else -> {
                    val groups = remember(state.products, submitted, category) {
                        when {
                            submitted.isNotEmpty() -> listOf("Search results" to state.products.matching(submitted))
                            category != null -> {
                                val ranked = state.products.inCategory(category.orEmpty())
                                listOf("Top rated" to ranked.take(1), "More in this category" to ranked.drop(1))
                            }
                            else -> listOf("Featured" to state.products.filter { it.featured })
                        }
                    }
                    val categories = remember(state.products) { state.products.map { it.category }.distinct() }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
                        item { Text(category ?: "Catalogue", style = MaterialTheme.typography.headlineLarge) }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(query, { query = it }, Modifier.weight(1f),
                                    label = { Text("Find products") }, singleLine = true)
                                Button(onClick = { submitted = query.trim(); category = null }) { Text("Search") }
                            }
                        }
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(categories) { label ->
                                    FilterChip(selected = category == label, onClick = {
                                        category = label
                                        submitted = ""
                                        query = ""
                                    }, label = { Text(label) })
                                }
                            }
                        }
                        groups.forEach { (heading, rows) ->
                            item { Text(heading, style = MaterialTheme.typography.titleLarge) }
                            if (rows.isEmpty()) item { Text("No products found.") }
                            items(rows, key = { it.id }) { row ->
                                Card(Modifier.fillMaxWidth().clickable { productId = row.id }) {
                                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(row.title, style = MaterialTheme.typography.titleMedium)
                                        Text(row.summary())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Product.summary() =
    NumberFormat.getCurrencyInstance(Locale.US).format(price) + "  ·  " + rating + " / 5"

@Composable
private fun EnquiryForm(product: Product, delivery: Delivery, submit: (Int, String, String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    val sending = delivery == Delivery.Sending
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (delivery == Delivery.Sent) {
            item { Text("Thank you", style = MaterialTheme.typography.headlineMedium) }
            item { Text("Your enquiry about " + product.title + " reached the sales inbox.") }
        } else {
            item { Text("Enquire about " + product.title, style = MaterialTheme.typography.headlineSmall) }
            item { OutlinedTextField(name, { name = it.take(100) }, label = { Text("Name") }, enabled = !sending, singleLine = true) }
            item { OutlinedTextField(email, { email = it.take(254) }, label = { Text("Email") }, enabled = !sending, singleLine = true) }
            if (delivery == Delivery.Failed) item { Text("Delivery failed. Please try again.") }
            item {
                Button(onClick = { submit(product.id, name, email) },
                    enabled = !sending && name.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    Text(if (sending) "Sending…" else "Send enquiry")
                }
            }
        }
    }
}

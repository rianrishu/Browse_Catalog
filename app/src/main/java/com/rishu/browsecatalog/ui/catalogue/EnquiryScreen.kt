package com.rishu.browsecatalog.ui.catalogue

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rishu.browsecatalog.domain.Product

@Composable
internal fun EnquiryScreen(product: Product, delivery: Delivery, submit: (Int, String, String) -> Unit,
    done: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val validEmail = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val sending = delivery == Delivery.Sending
    LazyColumn(Modifier.imePadding(), verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)) {
        if (delivery == Delivery.Sent) {
            item { Text("Thank you", style = MaterialTheme.typography.headlineLarge) }
            item { Text("Your enquiry about ${product.title} reached the sales inbox. Replies will go to $email.") }
            item { Button(onClick = done) { Text("Continue browsing") } }
        } else {
            item { Text("Product enquiry", style = MaterialTheme.typography.headlineLarge) }
            item { Text(product.title, style = MaterialTheme.typography.titleMedium) }
            item { ProductPrice(product) }
            item { HorizontalDivider() }
            item { OutlinedTextField(name, { name = it.take(100) }, Modifier.fillMaxWidth(), label = { Text("Name") },
                enabled = !sending, singleLine = true, isError = attempted && name.isBlank()) }
            item { OutlinedTextField(email, { email = it.take(254) }, Modifier.fillMaxWidth(), label = { Text("Email") },
                enabled = !sending, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = attempted && !validEmail) }
            if (attempted && (name.isBlank() || !validEmail)) item { Text("Enter your name and a valid email address.") }
            if (delivery == Delivery.Failed) item { Text("Couldn’t confirm delivery. Please try again.") }
            item { Button(onClick = {
                attempted = true
                if (name.isNotBlank() && validEmail) submit(product.id, name, email)
            }, enabled = !sending, modifier = Modifier.fillMaxWidth()) { Text(if (sending) "Sending…" else "Send enquiry") } }
        }
    }
}

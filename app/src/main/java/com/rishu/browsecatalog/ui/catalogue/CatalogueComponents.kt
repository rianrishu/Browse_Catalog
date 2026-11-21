package com.rishu.browsecatalog.ui.catalogue

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rishu.browsecatalog.domain.Product
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun ProductPhoto(product: Product, modifier: Modifier) {
    val url = product.imageUrl ?: return
    AndroidView(
        factory = { ImageView(it).apply { scaleType = ImageView.ScaleType.FIT_CENTER } },
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(10.dp),
        update = { view ->
            if (view.tag != url) {
                view.tag = url
                Picasso.get().load(url).fit().centerInside().into(view)
            }
        }
    )
}

@Composable
internal fun ProductPrice(product: Product) {
    val currency = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    Text(currency.format(product.price), style = MaterialTheme.typography.titleMedium)
}

@Composable
internal fun ProductRow(product: Product, open: (Int) -> Unit) {
    Surface(onClick = { open(product.id) }, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ProductPhoto(product, Modifier.size(84.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(product.title, maxLines = 2, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium)
                ProductPrice(product)
            }
            Text("★ " + product.rating, color = MaterialTheme.colorScheme.primary)
        }
    }
    HorizontalDivider()
}

@Composable
internal fun ProductSearch(query: String, edit: (String) -> Unit, search: () -> Unit) {
    val focus = LocalFocusManager.current
    val submit = { focus.clearFocus(); search() }
    OutlinedTextField(value = query, onValueChange = edit, modifier = Modifier.fillMaxWidth(),
        singleLine = true, label = { Text("Find products") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { submit() }),
        trailingIcon = { TextButton(onClick = submit) { Text("Search") } })
}

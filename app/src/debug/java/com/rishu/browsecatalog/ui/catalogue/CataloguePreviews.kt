package com.rishu.browsecatalog.ui.catalogue

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rishu.browsecatalog.domain.Product
import com.rishu.browsecatalog.ui.theme.BrowseCatalogTheme

private val bag = Product(1, "Canvas overnight bag", "Travel", "Cotton canvas with a lined interior and a removable shoulder strap.", 58.0, 4.8, true)
private val products = listOf(bag, bag.copy(id = 2, title = "Foldaway tote", price = 18.0, rating = 4.4))

@Composable
private fun Page(content: @Composable () -> Unit) {
    BrowseCatalogTheme { Surface(Modifier.padding(16.dp), content = content) }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun HomePreview() = Page { HomeScreen(products, "", {}, {}, {}, {}) }

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun CategoryPreview() = Page { CategoryScreen(products, "Travel") {} }

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SearchPreview() = Page { SearchResultsScreen(products, "canvas", "canvas", {}, {}, {}) }

@Preview(showBackground = true, widthDp = 360, heightDp = 680)
@Composable
private fun DetailPreview() = Page { ProductDetailScreen(bag) {} }

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EnquiryPreview() = Page { EnquiryScreen(bag, Delivery.Idle, { _, _, _ -> }) {} }

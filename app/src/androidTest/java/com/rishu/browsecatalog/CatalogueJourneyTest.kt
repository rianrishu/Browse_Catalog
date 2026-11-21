package com.rishu.browsecatalog

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.rishu.browsecatalog.domain.Catalogue
import com.rishu.browsecatalog.domain.Product
import com.rishu.browsecatalog.ui.catalogue.HomeScreen
import com.rishu.browsecatalog.ui.catalogue.CategoryScreen
import com.rishu.browsecatalog.ui.catalogue.CatalogueScreen
import com.rishu.browsecatalog.ui.catalogue.CatalogueViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CatalogueJourneyTest {
    @get:Rule val compose = createComposeRule()

    private val inventory = (1..7).map { id ->
        Product(id, "Product $id", if (id <= 3) "Travel" else "Home", "Item $id", 10.0,
            id.toDouble() / 2, id in listOf(1, 3, 4, 5, 7))
    }

    @Test fun homeDisplaysTheFiveSelectedProducts() {
        compose.setContent { HomeScreen(inventory, "", {}, {}, {}, {}) }
        val list = compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
        for (pick in inventory.filter { it.featured }) {
            list.performScrollToNode(hasText(pick.title))
            compose.onNodeWithText(pick.title).assertIsDisplayed()
        }
    }

    @Test fun categoryShowsBothEditorialPicksAndAdditionalProducts() {
        compose.setContent { CategoryScreen(inventory, "Travel") {} }
        val list = compose.onNode(hasScrollToIndexAction())
        for (text in listOf("Top rated", "Product 3", "Product 1", "More in this category", "Product 2")) {
            list.performScrollToNode(hasText(text))
            compose.onNodeWithText(text).assertIsDisplayed()
        }
        compose.onNodeWithText("Product 4").assertDoesNotExist()
    }

    @Test fun searchThenSendAnEnquiry() {
        var recipient = ""
        val product = Product(1, "Canvas bag", "Travel", "Overnight tote", 20.0, 4.5, true)
        val catalogue = object : Catalogue {
            override suspend fun products() = listOf(product)

            override suspend fun enquire(productId: Int, name: String, email: String) {
                assertEquals(product.id, productId)
                assertEquals("Sam", name)
                recipient = email
            }
        }
        compose.setContent {
            val model = androidx.compose.runtime.remember { CatalogueViewModel(catalogue) }
            CatalogueScreen(model)
        }
        compose.onNodeWithText("Find products").performTextInput("tote")
        compose.onNodeWithText("Search").performClick()
        compose.onNodeWithText("Search results").assertIsDisplayed()
        compose.onNodeWithText(product.title).performClick()
        compose.onNodeWithText("Enquire").performClick()
        compose.onNodeWithText("Name").performTextInput("Sam")
        compose.onNodeWithText("Email").performTextInput("sam@example.test")
        compose.onNodeWithText("Send enquiry").performClick()
        compose.onNodeWithText("Thank you").assertIsDisplayed()
        compose.runOnIdle { assertEquals("sam@example.test", recipient) }
    }
}

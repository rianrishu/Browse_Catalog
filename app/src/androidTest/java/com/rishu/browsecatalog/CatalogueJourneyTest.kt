package com.rishu.browsecatalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.rishu.browsecatalog.domain.Catalogue
import com.rishu.browsecatalog.domain.Product
import com.rishu.browsecatalog.ui.catalogue.CatalogueScreen
import com.rishu.browsecatalog.ui.catalogue.CatalogueViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CatalogueJourneyTest {
    @get:Rule val compose = createComposeRule()

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

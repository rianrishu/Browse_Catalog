package com.rishu.browsecatalog

import com.rishu.browsecatalog.domain.Product
import com.rishu.browsecatalog.domain.inCategory
import com.rishu.browsecatalog.domain.matching
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogueTest {
    private val bag = Product(1, "Daypack", "Bags", "Canvas", 20.0, 3.0, true)
    private val tote = bag.copy(id = 2, title = "Tote", description = "Cotton", rating = 5.0)
    private val products = listOf(bag, tote, tote.copy(id = 3, category = "Accessories"))

    @Test fun categoryRanksOnlyItsOwnProducts() {
        assertEquals(listOf(tote, bag), products.inCategory("Bags"))
    }

    @Test fun searchMatchesDescriptionsIgnoringCase() {
        assertEquals(listOf(bag), products.matching("CANVAS"))
    }
}

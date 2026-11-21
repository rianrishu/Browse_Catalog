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
        val extra = bag.copy(id = 4, featured = false)
        val category = (products + extra).inCategory("Bags")
        assertEquals(listOf(tote, bag), category.topRated)
        assertEquals(listOf(extra), category.additional)
    }

    @Test fun searchMatchesDescriptionsIgnoringCase() {
        assertEquals(listOf(bag), products.matching("CANVAS"))
    }

    @Test fun categoryWithoutPicksHighlightsTiedLeaders() {
        val tie = tote.copy(id = 4)
        val category = listOf(tie, bag, tote).map { it.copy(featured = false) }.inCategory("Bags")
        assertEquals(listOf(2, 4), category.topRated.map { it.id })
        assertEquals(listOf(1), category.additional.map { it.id })
    }

    @Test fun searchMatchesCategoryAndExcludesUnrelatedProducts() {
        assertEquals(listOf(bag, tote), products.matching("BAGS"))
        assertEquals(emptyList<Product>(), products.matching("camera"))
    }
}

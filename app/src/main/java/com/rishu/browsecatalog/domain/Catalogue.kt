package com.rishu.browsecatalog.domain

data class Product(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val featured: Boolean,
    val imagePath: String? = null
)

interface Catalogue {
    suspend fun products(): List<Product>
    suspend fun enquire(productId: Int, name: String, email: String)
}

fun List<Product>.matching(term: String) = filter {
    it.title.contains(term, ignoreCase = true) || it.description.contains(term, ignoreCase = true) ||
        it.category.contains(term, ignoreCase = true)
}

data class CategoryProducts(val topRated: List<Product>, val additional: List<Product>)

fun List<Product>.inCategory(category: String): CategoryProducts {
    val ranked = filter { it.category == category }
        .sortedWith(compareByDescending<Product> { it.rating }.thenBy { it.id })
    val (featured, additional) = ranked.partition { it.featured }
    if (featured.isNotEmpty()) return CategoryProducts(featured, additional)

    val highest = ranked.firstOrNull()?.rating
    val (top, rest) = ranked.partition { it.rating == highest }
    return CategoryProducts(top, rest)
}

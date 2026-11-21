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
    it.title.contains(term, ignoreCase = true) || it.description.contains(term, ignoreCase = true)
}

fun List<Product>.inCategory(category: String) =
    filter { it.category == category }.sortedWith(compareByDescending<Product> { it.rating }.thenBy { it.id })

package com.rishu.browsecatalog.data

import androidx.annotation.Keep
import com.rishu.browsecatalog.domain.Catalogue
import com.rishu.browsecatalog.domain.Product
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Keep
internal data class EnquiryMessage(val productId: Int, val title: String, val name: String, val email: String)

@Keep
internal data class StoreProduct(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val featured: Boolean,
    val imagePath: String?
)

internal interface StoreApi {
    @GET("products")
    suspend fun products(): List<StoreProduct>

    @POST("enquiries")
    suspend fun enquire(@Body message: EnquiryMessage)
}

private const val catalogueOrigin = "http://127.0.0.1:3000"

internal class RemoteCatalogue(private val store: StoreApi) : Catalogue {
    override suspend fun products() = store.products().map { item ->
        Product(item.id, item.title, item.category, item.description, item.price, item.rating,
            item.featured, item.imagePath?.takeIf { it.startsWith("/img/") }?.let { catalogueOrigin + it })
    }

    override suspend fun enquire(product: Product, name: String, email: String) =
        store.enquire(EnquiryMessage(product.id, product.title, name.trim(), email.trim()))
}

internal fun createCatalogue(client: OkHttpClient = catalogueHttpClient()): Catalogue {
    val retrofit = Retrofit.Builder()
        .baseUrl("$catalogueOrigin/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return RemoteCatalogue(retrofit.create(StoreApi::class.java))
}

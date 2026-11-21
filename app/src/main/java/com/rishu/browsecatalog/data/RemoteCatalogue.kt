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
internal data class Contact(val productId: Int, val name: String, val email: String)

@Keep
internal data class StoreRating(val rate: Double)

@Keep
internal data class StoreProduct(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val price: Double,
    val rating: StoreRating,
    val image: String?
)

internal interface StoreApi {
    @GET("products")
    suspend fun products(): List<StoreProduct>
}

internal interface EnquiryApi {
    @POST("enquiries")
    suspend fun enquire(@Body contact: Contact)
}

private val featuredIds = setOf(1, 5, 9, 14, 18)

internal class RemoteCatalogue(private val store: StoreApi, private val enquiries: EnquiryApi) : Catalogue {
    override suspend fun products() = store.products().map { item ->
        Product(item.id, item.title, item.category, item.description, item.price, item.rating.rate,
            item.id in featuredIds, item.image?.takeIf { it.startsWith("https://") })
    }

    override suspend fun enquire(productId: Int, name: String, email: String) =
        enquiries.enquire(Contact(productId, name.trim(), email.trim()))
}

internal fun createCatalogue(client: OkHttpClient = OkHttpClient()): Catalogue {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val enquiries = retrofit.newBuilder().baseUrl("http://127.0.0.1:3000/").build()
    return RemoteCatalogue(retrofit.create(StoreApi::class.java), enquiries.create(EnquiryApi::class.java))
}

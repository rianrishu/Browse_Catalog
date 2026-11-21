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

internal interface CatalogueApi {
    @GET("products")
    suspend fun products(): List<Product>

    @POST("enquiries")
    suspend fun enquire(@Body contact: Contact)
}

internal class RemoteCatalogue(private val api: CatalogueApi) : Catalogue {
    override suspend fun products() = api.products()

    override suspend fun enquire(productId: Int, name: String, email: String) =
        api.enquire(Contact(productId, name.trim(), email.trim()))
}

internal fun createCatalogue(): Catalogue {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://127.0.0.1:3000/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return RemoteCatalogue(retrofit.create(CatalogueApi::class.java))
}

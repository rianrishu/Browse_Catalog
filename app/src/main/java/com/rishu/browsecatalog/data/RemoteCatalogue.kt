package com.rishu.browsecatalog.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.rishu.browsecatalog.domain.Catalogue
import com.rishu.browsecatalog.domain.Product
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Keep
internal data class MailAddress(@SerializedName("Email") val email: String)

@Keep
internal data class EnquiryMessage(
    @SerializedName("From") val from: MailAddress,
    @SerializedName("To") val to: List<MailAddress>,
    @SerializedName("ReplyTo") val replyTo: List<MailAddress>,
    @SerializedName("Subject") val subject: String,
    @SerializedName("Text") val text: String
)

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
}

internal interface EnquiryApi {
    @POST("api/v1/send")
    suspend fun enquire(@Body message: EnquiryMessage)
}

private const val catalogueOrigin = "http://127.0.0.1:3000"

internal class RemoteCatalogue(private val store: StoreApi, private val enquiries: EnquiryApi) : Catalogue {
    override suspend fun products() = store.products().map { item ->
        Product(item.id, item.title, item.category, item.description, item.price, item.rating,
            item.featured, item.imagePath?.takeIf { it.startsWith("/img/") }?.let { catalogueOrigin + it })
    }

    override suspend fun enquire(product: Product, name: String, email: String) =
        enquiries.enquire(
            EnquiryMessage(
                from = MailAddress("catalogue@example.test"),
                to = listOf(MailAddress("sales@example.test")),
                replyTo = listOf(MailAddress(email.trim())),
                subject = "Product enquiry #${product.id}",
                text = "${name.trim()} is interested in ${product.title}. Contact: ${email.trim()}"
            )
        )
}

internal fun createCatalogue(client: OkHttpClient = OkHttpClient()): Catalogue {
    val retrofit = Retrofit.Builder()
        .baseUrl("$catalogueOrigin/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val enquiries = retrofit.newBuilder().baseUrl("http://127.0.0.1:8025/").build()
    return RemoteCatalogue(retrofit.create(StoreApi::class.java), enquiries.create(EnquiryApi::class.java))
}

package com.rishu.browsecatalog

import com.rishu.browsecatalog.data.createCatalogue
import com.google.gson.JsonParser
import com.rishu.browsecatalog.domain.Product
import kotlinx.coroutines.runBlocking
import okio.Buffer
import retrofit2.HttpException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogueApiTest {
    @Test fun browsingUsesBackendForProductsAndPhotos() = runBlocking {
        val photo = "/img/bag.jpg"
        val body = """[{"id":1,"title":"Bag","category":"Travel","description":"Canvas","price":15,
            "rating":4.8,"featured":true,"imagePath":"$photo"}]"""
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            assertEquals("http://127.0.0.1:3000/products", chain.request().url.toString())
            Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200)
                .message("OK").body(body.toResponseBody("application/json".toMediaType())).build()
        }.build()

        val product = createCatalogue(client).products().single()
        assertEquals(4.8, product.rating, 0.0)
        assertEquals("http://127.0.0.1:3000$photo", product.imageUrl)
        assertEquals(true, product.featured)
    }

    @Test fun enquiryPostsTheProductAndReplyAddressToTheLocalInbox() = runBlocking {
        var url = ""
        var json = ""
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            url = request.url.toString()
            json = Buffer().apply { requireNotNull(request.body).writeTo(this) }.readUtf8()
            Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(200)
                .message("OK").body("""{"ID":"accepted"}""".toResponseBody()).build()
        }.build()
        val bag = Product(12, "Wool bag", "Bags", "Canvas", 19.0, 4.0, false)
        createCatalogue(client).enquire(bag, " Sam ", " sam@example.test ")

        assertEquals("http://127.0.0.1:8025/api/v1/send", url)
        val message = JsonParser.parseString(json).asJsonObject
        assertEquals("sales@example.test", message["To"].asJsonArray.single().asJsonObject["Email"].asString)
        assertEquals("sam@example.test", message["ReplyTo"].asJsonArray.single().asJsonObject["Email"].asString)
        assertEquals("Product enquiry #12", message["Subject"].asString)
        assertEquals("Sam is interested in Wool bag. Contact: sam@example.test", message["Text"].asString)
    }

    @Test(expected = HttpException::class)
    fun rejectedEnquiryPropagatesToTheScreen() = runBlocking {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(503)
                .message("Unavailable").body("".toResponseBody()).build()
        }.build()
        createCatalogue(client).enquire(Product(1, "Bag", "Travel", "", 10.0, 4.0, true), "Sam", "sam@example.test")
    }
}

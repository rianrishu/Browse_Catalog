package com.rishu.browsecatalog

import com.rishu.browsecatalog.data.createCatalogue
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class DirectStoreTest {
    @Test fun browsingCallsFakeStoreAndMapsItsProductFormat() = runBlocking {
        val photo = "https://fakestoreapi.com/img/bag.jpg"
        val body = """[{"id":1,"title":"Bag","category":"Travel","description":"Canvas","price":15,
            "rating":{"rate":4.8,"count":6},"image":"$photo"}]"""
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            assertEquals("https://fakestoreapi.com/products", chain.request().url.toString())
            Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200)
                .message("OK").body(body.toResponseBody("application/json".toMediaType())).build()
        }.build()

        val product = createCatalogue(client).products().single()
        assertEquals(4.8, product.rating, 0.0)
        assertEquals(photo, product.imageUrl)
        assertEquals(true, product.featured)
    }
}

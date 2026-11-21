package com.rishu.browsecatalog.data

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal fun catalogueHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
    logging.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder().addInterceptor(logging).build()
}

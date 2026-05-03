package com.example.mvcpokedex.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.mvcpokedex.BuildConfig


object RetrofitClient {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    private const val TAG = "RetrofitClient"
    private const val TIMEOUT_SECONDS = 30L

    // Logging interceptor — only active in debug builds
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY  // logs everything in debug
        } else {
            HttpLoggingInterceptor.Level.NONE  // logs nothing in release
        }
    }

    // OkHttp client — handles actual HTTP connections
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)  // connecting to server
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)     // reading response
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)    // sending request
        .build()

    val apiService: ApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
package com.example.androidtest.countries.service

import com.example.androidtest.countries.CountriesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class CountriesService {
    @OptIn(ExperimentalSerializationApi::class)
    val json by lazy {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            // explicitNulls, defines whether null property
            // values should be included in the serialized JSON string.
            explicitNulls = false

            // If API returns null or unknown values for Enums, we can use default constructor parameter to override it
            // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#coercing-input-values
            coerceInputValues = true
        }
    }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                .apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    private val contentType = "application/json; charset=utf-8".toMediaType()

    private val countriesRetrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val countriesApi: CountriesApi
        get() = countriesRetrofit.create(CountriesApi::class.java)

    private companion object {
        private const val BASE_URL = "https://restcountries.com/v3.1/"
    }
}

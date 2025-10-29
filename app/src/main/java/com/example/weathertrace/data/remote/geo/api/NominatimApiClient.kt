package com.example.weathertrace.data.remote.geo.api

import com.example.weathertrace.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


object NominatimApiClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "WeatherTraceApp/1.0 (${BuildConfig.DEV_MAIL})")
                .header("Accept-Language", "en")
                .build()
            chain.proceed(request)
        })
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val nominatimService: NominatimService by lazy {
        retrofit.create(NominatimService::class.java)
    }
}

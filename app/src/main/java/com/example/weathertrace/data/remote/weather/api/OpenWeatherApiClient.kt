package com.example.weathertrace.data.remote.weather.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import com.example.weathertrace.data.remote.weather.api.OpenWeatherService

object openWeatherApiClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val openWeatherService: OpenWeatherService by lazy {
        retrofit.create(OpenWeatherService::class.java)
    }
}

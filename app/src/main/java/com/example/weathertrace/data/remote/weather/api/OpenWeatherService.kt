package com.example.weathertrace.data.remote.weather.api
import com.example.weathertrace.data.remote.weather.dto.*

import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate


interface OpenWeatherService {
    @GET("data/3.0/onecall/day_summary")
    suspend fun getDailyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("date") date: LocalDate,
        @Query("appid") apiKey: String,
        @Query("units") units: String? = null,
        @Query("lang") lang: String? = null
    ): DailyWeatherDto
}
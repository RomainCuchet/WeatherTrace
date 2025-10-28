package com.example.weathertrace.data.remote.weather.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    val lat: Double,
    val lon: Double,
    val tz: String,
    val date: String,
    val units: String,

    @Json(name = "cloud_cover")
    val cloudCover: Afternoon<Double>,

    val humidity: Afternoon<Double>,
    val precipitation: Total<Double>,
    val pressure: Afternoon<Double>,
    val temperature: TemperatureDto,
    val wind: WindDto
)

@JsonClass(generateAdapter = true)
data class Afternoon<T>(val afternoon: T)

@JsonClass(generateAdapter = true)
data class Total<T>(val total: T)

@JsonClass(generateAdapter = true)
data class TemperatureDto(
    val min: Double,      // Min temp
    val max: Double,      // Max temp
    val afternoon: Double,
    val night: Double,
    val evening: Double,
    val morning: Double
)

@JsonClass(generateAdapter = true)
data class WindDto(
    val max: MaxWindDto
)

@JsonClass(generateAdapter = true)
data class MaxWindDto(
    val speed: Double,     // Max wind speed
    val direction: Double     // Wind direction in degrees
)

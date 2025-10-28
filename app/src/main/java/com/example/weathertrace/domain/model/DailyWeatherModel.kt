package com.example.weathertrace.domain.model
import com.example.weathertrace.BuildConfig

data class DailyWeatherModel(
    val date: String,
    val cloudCover: Afternoon<Double>,
    val humidity: Afternoon<Double>,
    val precipitation: Total<Double>,
    val pressure: Afternoon<Double>,
    val temperature: Temperature,
    val wind: Wind
)

data class Afternoon<T>(val afternoon: T)
data class Total<T>(val total: T)

data class Temperature(
    val min: Double,
    val max: Double,
    val afternoon: Double,
    val night: Double,
    val evening: Double,
    val morning: Double
)

data class Wind(
    val max: MaxWind
)

data class MaxWind(
    val speed: Double,
    val direction: Double
)
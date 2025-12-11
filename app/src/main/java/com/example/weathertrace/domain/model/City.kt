package com.example.weathertrace.domain.model

data class City(
    val name: String,
    val lat: Double,
    val lon: Double,
    val isFavorite: Boolean = false
)
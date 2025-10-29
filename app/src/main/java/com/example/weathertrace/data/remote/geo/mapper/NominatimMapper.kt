package com.example.weathertrace.data.mapper

import com.example.weathertrace.data.remote.geo.dto.CityResponse
import com.example.weathertrace.domain.model.City

fun CityResponse.toDomain(): City {
    return City(
        name = display_name,
        lat = lat.toDoubleOrNull() ?: 0.0,
        lon = lon.toDoubleOrNull() ?: 0.0
    )
}

fun List<CityResponse>.toDomainList(): List<City> {
    return map { it.toDomain() }
}

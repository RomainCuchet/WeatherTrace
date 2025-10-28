package com.example.weathertrace.domain.repository

import com.example.weathertrace.data.mapper.toDomain
import com.example.weathertrace.data.remote.weather.api.openWeatherApiClient
import com.example.weathertrace.domain.model.*

class WeatherRepository(
    private val devMode: Boolean = true // 🔧 Flag développeur activable/désactivable
) {

    private val apiService = openWeatherApiClient.openWeatherService

    suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        date: String,
        apiKey: String,
        units: String? = null,
        lang: String? = null
    ): DailyWeatherModel {
        return if (devMode) {
            println("⚙️ Dev Mod activated : using mocked data")
            getMockWeather(date)
        } else {
            apiService.getDailyWeather(lat, lon, date, apiKey, units, lang).toDomain()
        }
    }

    private fun getMockWeather(date: String): DailyWeatherModel {
        return DailyWeatherModel(
            date = date,
            cloudCover = Afternoon<Double>(45.0),
            humidity = Afternoon<Double>(62.0),
            precipitation = Total<Double>(2.5),
            pressure = Afternoon<Double>(1018.0),
            temperature = Temperature(
                min = 12.3,
                max = 22.8,
                afternoon = 20.5,
                night = 14.2,
                evening = 17.1,
                morning = 13.7
            ),
            wind = Wind(
                max = MaxWind(
                    speed = 18.0,
                    direction = 232.0
                )
            )
        )
    }
}
